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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

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
        return BookResponse.of(savedBook);
    }

    // 아이디로 도서 찾기
    @Override
    @Transactional(readOnly = true)
    public BookDetailResponse getBookDetailByBookId(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return BookDetailResponse.of(book);
    }


    // 작가로 도서 찾기
    @Override
    @Transactional(readOnly = true)
    public List<BookDetailResponse> getBooksByAuthor(String author) {
        List<Book> books = bookRepository.findByAuthor(author);
        return books.stream().map(BookDetailResponse::of).toList();
    }

    // 출판사로 도서 찾기
    @Override
    public List<BookDetailResponse> getBooksByPublisher(String publisher) {
        List<Book> books = bookRepository.findByPublisher(publisher);
        return books.stream().map(BookDetailResponse::of).toList();
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

        return BookResponse.of(book);
    }

    // 도서 삭제
    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookRepository.delete(book);
    }
}
