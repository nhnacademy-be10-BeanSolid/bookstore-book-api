package com.nhnacademy.bookapi.bookcategory.exception;

public class BookCategoryMapNotFoundException extends RuntimeException {

    public BookCategoryMapNotFoundException(Long bookId, Long categoryId) {
        super(String.format("Book id %s category id %s not exists", bookId, categoryId));
    }
}
