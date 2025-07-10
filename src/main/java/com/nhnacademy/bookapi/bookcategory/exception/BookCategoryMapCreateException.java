package com.nhnacademy.bookapi.bookcategory.exception;

import com.nhnacademy.bookapi.common.exception.BadRequestException;

public class BookCategoryMapCreateException extends BadRequestException {
    public BookCategoryMapCreateException(Long bookId) {
        super(String.format("Book ID: %d  - Book category count over 10", bookId));
    }
}
