package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookResponse createBook(BookCreateRequest request);

    BookResponse getBookResponseByBookId(Long id);

    BookDetailResponse getBookDetailResponseByBookId(Long id);

    Page<BookResponse> getAllBooks(Pageable pageable);

    Page<BookResponse> getBooksResponseByAuthor(String author, Pageable pageable);

    Page<BookResponse> getBooksResponseByPublisher(String publisher, Pageable pageable);

    BookResponse updateBook(Long id, BookUpdateRequest request);

    void deleteBook(Long id);

    Page<BookResponse> getBooksResponseByTag(String tag, Pageable pageable);

    // 도서 제목으로 검색
    Page<BookResponse> getBookResponseByTitle(String title, Pageable pageable);

    // 도서 설명으로 검색
    Page<BookResponse> getBookResponseByDescription(String description, Pageable pageable);
}
