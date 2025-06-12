package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.controller.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.controller.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.controller.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.controller.response.BookResponse;

import java.util.List;

public interface BookService {

    BookResponse createBook(BookCreateRequest request);

    BookDetailResponse getBookDetail(String isbn);

    BookResponse getBook(String isbn);

    List<BookResponse> getBooksByAuthor(String author);

    List<BookResponse> getBooksByPublisher(String title);

    BookResponse updateBook(String isbn, BookUpdateRequest request);

    void deleteBook(String isbn);

}
