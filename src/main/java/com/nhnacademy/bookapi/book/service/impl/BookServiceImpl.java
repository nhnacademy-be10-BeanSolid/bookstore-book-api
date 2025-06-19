package com.nhnacademy.bookapi.book.service.impl;

import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.exception.BookAlreadyExistsException;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.book.service.BookService;
import com.nhnacademy.bookapi.booktag.repository.BookTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookTagRepository tagRepository;

    // 도서 추가
    @Override
    public BookResponse createBook(BookCreateRequest request) {
        if(bookRepository.existsByIsbn(request.isbn())) {
            throw new BookAlreadyExistsException(request.isbn());
        }

        Book book = Book.builder()
                .title(request.title())
                .description(request.description())
                .toc(request.toc())
                .publisher(request.publisher())
                .author(request.author())
                .publishedDate(request.publishedDate())
                .isbn(request.isbn())
                .originalPrice(request.originalPrice())
                .salePrice(request.salePrice())
                .wrappable(request.wrappable())
                .stock(request.stock())
                .build();
        Book savedBook = bookRepository.save(book);
        return bookRepository.findBookResponseById(savedBook.getId())
                .orElseThrow(() -> new BookNotFoundException(savedBook.getId()));
    }

    // 아이디로 도서 찾기
    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookResponseByBookId(Long id) {
        return bookRepository.findBookResponseById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    // 작가로 도서 찾기
    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getBooksResponseByAuthor(String author, Pageable pageable) {
        return bookRepository.findBookResponsesByAuthor(author, pageable);
    }

    // 출판사로 도서 찾기
    @Override
    public Page<BookResponse> getBooksResponseByPublisher(String publisher, Pageable pageable) {
        return bookRepository.findBookResponseByPublisher(publisher, pageable);
    }

    // 도서 업데이트
    @Override
    public BookResponse updateBook(Long id, BookUpdateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            book.setDescription(request.getDescription());
        }
        if (request.getToc() != null) {
            book.setToc(request.getToc());
        }
        if (request.getPublisher() != null) {
            book.setPublisher(request.getPublisher());
        }
        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }
        if (request.getPublishedDate() != null) {
            book.setPublishedDate(request.getPublishedDate());
        }
        if (request.getIsbn() != null) {
            book.setIsbn(request.getIsbn());
        }
        if (request.getOriginalPrice() != null) {
            book.setOriginalPrice(request.getOriginalPrice());
        }
        if (request.getSalePrice() != null) {
            book.setSalePrice(request.getSalePrice());
        }
        if (request.getStock() != null) {
            book.setStock(request.getStock());
        }
        if (request.getStatus() != null) {
            book.setStatus(BookStatus.from(request.getStatus()));
        }
        book.setUpdateAt(LocalDateTime.now());

        return bookRepository.findBookResponseById(book.getId())
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    // 도서 삭제
    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookRepository.delete(book);
    }

    // 태그로 도서 검색
    @Override
    public Page<BookResponse> getBooksResponseByTag(String tag, Pageable pageable) {
        log.info("태그 파라미터 {}", tag);
        return bookRepository.findBookResponseByTag(tag, pageable);
    }

    // 도서 상세정보 (좋아요한 유저까지 포함)
    @Override
    public BookDetailResponse getBookDetailResponseByBookId(Long id) {
        return bookRepository.findBookDetailResponseByBookId(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    // 도서 이름(타이틀)로 검색
    public Page<BookResponse> getBookResponseByTitle(String title, Pageable pageable) {
        return bookRepository.findBookResponseByTitle(title, pageable);
    }

    // 도서 설명으로 검색
    @Override
    public Page<BookResponse> getBookResponseByDescription(String description, Pageable pageable) {
        return bookRepository.findBookResponseByDescription(description, pageable);
    }
}
