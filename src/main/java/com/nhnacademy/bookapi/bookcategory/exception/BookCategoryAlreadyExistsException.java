package com.nhnacademy.bookapi.bookcategory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookCategoryAlreadyExistsException extends RuntimeException {
    public BookCategoryAlreadyExistsException(String categoryName) {
        super("Category '" + categoryName + "' already exists");
    }
}
