package com.nhnacademy.bookapi.exception;

public class BookCategoryNotFoundException extends RuntimeException {
    public BookCategoryNotFoundException(Long categoryId) {
        super("Book category with id " + categoryId + " not found.");
    }
}
