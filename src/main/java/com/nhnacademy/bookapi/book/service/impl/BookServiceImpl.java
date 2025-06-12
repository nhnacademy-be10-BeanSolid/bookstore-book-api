package com.nhnacademy.bookapi.book.service.impl;

import com.nhnacademy.bookapi.book.controller.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.controller.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.controller.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.controller.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.exception.AuthorNotFoundException;
import com.nhnacademy.bookapi.book.exception.BookAlreadyExistsException;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.exception.PublisherNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    // 도서 추가
    @Override
    public BookResponse createBook(BookCreateRequest request) {
        if(bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BookAlreadyExistsException(request.getIsbn());
        }

        Book book = new Book(request.getTitle(),
                request.getDescription(),
                request.getToc(),
                request.getPublisher(),
                request.getAuthor(),
                request.getPublishedDate(),
                request.getIsbn(),
                request.getOriginalPrice(),
                request.getSalePrice(),
                request.getWrappable(),
                request.getStock()
                );

        Book savedBook = bookRepository.save(book);

        return BookResponse.of(savedBook);
    }

    // 도서 상세정보
    @Override
    public BookDetailResponse getBookDetail(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        return BookDetailResponse.of(book);
    }

    // 국제표준도서번호로 도서 찾기
    @Override
    @Transactional(readOnly = true)
    public BookResponse getBook(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        return BookResponse.of(book);
    }

    // 작가로 도서 찾기
    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByAuthor(String author) {
        if(!bookRepository.existsByAuthor(author)) {
            throw new AuthorNotFoundException(author);
        }
        List<Book> books = bookRepository.findByAuthor(author);
        return books.stream().map(BookResponse::of).collect(Collectors.toList());
    }

    // 출판사로 도서 찾기
    @Override
    public List<BookResponse> getBooksByPublisher(String publisher) {
        if(!bookRepository.existsByPublisher(publisher)) {
            throw new PublisherNotFoundException(publisher);
        }
        List<Book> books = bookRepository.findByPublisher(publisher);
        return books.stream().map(BookResponse::of).collect(Collectors.toList());
    }

    // 도서 업데이트
    @Override
    public BookResponse updateBook(String isbn, BookUpdateRequest request) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
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

        return BookResponse.of(book);
    }

    // 도서 삭제
    @Override
    public void deleteBook(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        bookRepository.delete(book);
    }
}
