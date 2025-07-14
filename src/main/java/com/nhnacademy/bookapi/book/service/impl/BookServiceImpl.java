package com.nhnacademy.bookapi.book.service.impl;

import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookStockReduceRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookOrderResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.response.SimpleBookResponse;
import com.nhnacademy.bookapi.book.exception.BookAlreadyExistsException;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.exception.BookNotSaleException;
import com.nhnacademy.bookapi.book.exception.InsufficientStockException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.book.service.BookService;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryNotFoundException;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.document.repository.BookDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookDocumentRepository bookDocumentRepository;

    // 도서 추가
    @Override
    public BookResponse createBook(BookCreateRequest request) {
        if(bookRepository.existsByIsbn(request.isbn())) {
            throw new BookAlreadyExistsException(request.isbn());
        }

        Set<BookCategory> categories = request.categoryIds().stream()
                .map(id -> bookCategoryRepository.findById(id)
                        .orElseThrow(() -> new BookCategoryNotFoundException(id)))
                .collect(Collectors.toSet());

        String image = request.image();

        log.info("image: {}", image);

        if (image == null || image.isEmpty()) {
            image = "/images/default.png";
        }

        log.info("toc: {}", request.toc());

        Book book = Book.from(request, categories);
        book.setImage(image);
        Book savedBook = bookRepository.save(book);

        log.info("saved book: {}", savedBook.getToc());

        // Elastic Search에 저장
        BookDocument document = BookDocument.from(savedBook);
        bookDocumentRepository.save(document);

        log.info("Book created: {}", document);

        return bookRepository.findBookResponseById(savedBook.getId())
                .orElseThrow(() -> new BookNotFoundException(savedBook.getId()));
    }

    // 도서 상세정보 (좋아요한 유저까지 포함)
    @Override
    @Transactional(readOnly = true)
    public BookDetailResponse getBookDetailResponseByBookId(Long id) {
        return bookRepository.findBookDetailResponseByBookId(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    // 전체 리스트
    @Override
    @Transactional(readOnly = true)
    public Page<SimpleBookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAllSimpleBookResponses(pageable);
    }

    // 도서 업데이트
    @Override
    public BookDetailResponse updateBook(Long id, BookUpdateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        book.updateFrom(request);

        BookDocument updateDocument = BookDocument.from(book);
        bookDocumentRepository.save(updateDocument);

        return bookRepository.findBookDetailResponseByBookId(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    // 도서 삭제
    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookDocumentRepository.deleteById(String.valueOf(book.getId())); // 인덱스 다시 저장
        bookRepository.delete(book);
    }

    // 검색
    @Override
    public Page<SimpleBookResponse> getBookDocumentByKeyword(String keyword, Pageable pageable) {
        return bookDocumentRepository.searchByKeyword(keyword, pageable);
    }

    // 주문 api 에 정보 전달
    @Override
    public List<BookOrderResponse> getBookOrderResponseByBookIds(List<Long> ids) {
        List<BookOrderResponse> response = bookRepository.findBookOrderResponsesById(ids);

        Set<Long> responseIds = response.stream()
                .map(BookOrderResponse::id)
                .collect(Collectors.toSet());

        Set<Long> notSaleIds = new HashSet<>();

        for(Long id : ids){
            if(!responseIds.contains(id)){
                notSaleIds.add(id);
            }
        }

        if(!notSaleIds.isEmpty()){
            throw new BookNotSaleException(notSaleIds);
        }

        return response;
    }

    // 결제 후 재고 최신화
    // 동시성 문제
    @Override
    public void updateBookStock(List<BookStockReduceRequest> requests) {
        for (BookStockReduceRequest request : requests) {
            Long bookId = request.bookId();
            Integer stock = request.stock();

            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException(bookId));

            if (book.getStock() < stock) {
                log.info("{} 재고차감 실패 ", bookId);
                throw new InsufficientStockException(bookId);
            }

            book.setStock(book.getStock() - stock);
            bookRepository.save(book);
            log.info("Id {}의 재고 {} 차감 성공", bookId, stock);
        }
    }
}
