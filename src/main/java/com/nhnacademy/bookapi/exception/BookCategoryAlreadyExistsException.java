package com.nhnacademy.bookapi.exception;

public class BookCategoryAlreadyExistsException extends RuntimeException {
    public BookCategoryAlreadyExistsException(String categoryName) {
        super("Category '" + categoryName + "' already exists");
    }
}
