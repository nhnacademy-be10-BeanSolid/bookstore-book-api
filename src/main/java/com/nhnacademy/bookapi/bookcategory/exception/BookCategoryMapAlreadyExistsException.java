package com.nhnacademy.bookapi.bookcategory.exception;

import com.nhnacademy.bookapi.common.exception.ConflictException;

public class BookCategoryMapAlreadyExistsException extends ConflictException {
    public BookCategoryMapAlreadyExistsException(Long bookId, Long categoryId) {
        super(String.format("Book id %s category id %s already exists", bookId, categoryId));
    }
}
