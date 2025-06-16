package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;

import java.util.List;

public interface BookService {

    BookResponse createBook(BookCreateRequest request);

    BookDetailResponse getBookDetailByBookId(Long id);

    List<BookDetailResponse> getBooksByAuthor(String author);

    List<BookDetailResponse> getBooksByPublisher(String publisher);

    BookResponse updateBook(Long id, BookUpdateRequest request);

    void deleteBook(Long id);

}
