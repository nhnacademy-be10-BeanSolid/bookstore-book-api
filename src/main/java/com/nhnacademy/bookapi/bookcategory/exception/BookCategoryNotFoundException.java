package com.nhnacademy.bookapi.bookcategory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookCategoryNotFoundException extends RuntimeException {
    public BookCategoryNotFoundException(Long categoryId) {
        super("Book category with id " + categoryId + " not found.");
    }
}
