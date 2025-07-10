package com.nhnacademy.bookapi.bookcategory.exception;

import com.nhnacademy.bookapi.common.exception.NotFoundException;

public class BookCategoryMapNotFoundException extends NotFoundException {
    public BookCategoryMapNotFoundException(Long bookId, Long categoryId) {
        super(String.format("Book id %s category id %s not found", bookId, categoryId));
    }
}
