package com.nhnacademy.bookapi.bookcategory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookCategoryMapCreateException extends RuntimeException {
    public BookCategoryMapCreateException(Long bookId, String title) {
        super("Book ID: " + bookId + ", Title: " + title + " Book category count over 10");
    }
}
