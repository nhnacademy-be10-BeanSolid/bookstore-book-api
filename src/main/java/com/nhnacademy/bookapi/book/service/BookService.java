package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.controller.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.controller.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.controller.response.BookResponse;

import java.util.List;

public interface BookService {

    public BookResponse createBook(BookCreateRequest request);

    public BookResponse getBook(String isbn);

    public List<BookResponse> getBooksByAuthor(String author);

    public List<BookResponse> getBooksByPublisher(String title);

    public BookResponse updateBook(String isbn, BookUpdateRequest request);

    public void deleteBook(String isbn);
}
