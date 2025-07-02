package com.nhnacademy.bookapi.bookcategory.exception;

import com.nhnacademy.bookapi.common.exception.ConflictException;

public class BookCategoryAlreadyExistsException extends ConflictException {
    public BookCategoryAlreadyExistsException(String categoryName) {
        super(String.format("Category '%s' already exists", categoryName));
    }
}
