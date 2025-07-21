package com.nhnacademy.bookapi.book.service.impl;

import com.nhnacademy.bookapi.event.BookCreateEvent;
import com.nhnacademy.bookapi.event.BookDeleteEvent;
import com.nhnacademy.bookapi.event.BookUpdateEvent;
import com.nhnacademy.bookapi.event.BookViewEvent;
import com.nhnacademy.bookapi.adpater.service.UserService;
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
import com.nhnacademy.bookapi.document.repository.BookDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final UserService userService;

    // 이벤트 발행용 인터페이스
    private final ApplicationEventPublisher applicationEventPublisher;

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

        if (image == null || image.isEmpty()) {
            image = "/images/default.png";
        }

        Book book = Book.from(request, categories);
        book.setImage(image);
        Book savedBook = bookRepository.save(book);

        applicationEventPublisher.publishEvent(new BookCreateEvent(savedBook));

        return bookRepository.findBookResponseById(savedBook.getId())
                .orElseThrow(() -> new BookNotFoundException(savedBook.getId()));
    }

    // 도서 상세정보
    @Override
    @Transactional(readOnly = true)
    public BookDetailResponse getBookDetailResponseByBookId(Long id) {
        return bookRepository.findBookDetailResponseByBookId(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    // 조회 카운트 증가
    @Override
    public void increaseViewCount(Long id) {
        bookRepository.incrementViewCount(id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        applicationEventPublisher.publishEvent(new BookViewEvent(book));
    }

    // 전체 리스트
    @Override
    @Transactional(readOnly = true)
    public Page<SimpleBookResponse> getAllBooks(Pageable pageable) {
        return bookDocumentRepository.findAllSimpleBookResponses(pageable);
    }

    // 카테고리를 가지고 있는 도서 리스트
    @Override
    @Transactional(readOnly = true)
    public Page<SimpleBookResponse> getAllBooks(Long categoryId, Pageable pageable) {
        return bookDocumentRepository.findAllSimpleBookResponses(categoryId, pageable);
    }

    // 도서 업데이트
    @Override
    public BookDetailResponse updateBook(Long id, BookUpdateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        book.updateFrom(request);

        Long reviewCount = userService.countReviewsByBookId(book.getId());
        Double reviewAverage = userService.getAverageEvaluationScoreByBookId(book.getId());
        applicationEventPublisher.publishEvent(new BookUpdateEvent(book, reviewCount, reviewAverage));

        return bookRepository.findBookDetailResponseByBookId(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    // 도서 삭제
    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        bookRepository.delete(book);
        applicationEventPublisher.publishEvent(new BookDeleteEvent(book));
    }

    // 검색
    @Override
    public Page<SimpleBookResponse> getSimpleBookResponseByKeyword(String keyword, Pageable pageable) {
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

    // 유저 서비스에서 필요한 정보 - 아이디로 책이름 반환
    @Override
    @Transactional(readOnly = true)
    public String getTitleByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        return book.getTitle();
    }

    // 유저 서비스에서 최신화
    @Override
    @Transactional(readOnly = true)
    public void updateBookDocument(Long bookId, Long reviewCount, Double reviewAverage) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        applicationEventPublisher.publishEvent(new BookUpdateEvent(book, reviewCount, reviewAverage));
    }
}
