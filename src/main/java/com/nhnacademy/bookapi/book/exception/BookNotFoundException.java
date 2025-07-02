package com.nhnacademy.bookapi.book.exception;

import com.nhnacademy.bookapi.common.exception.NotFoundException;

public class BookNotFoundException extends NotFoundException {
    public BookNotFoundException(Long id) {
        super(String.format("Book ID : %d not found", id));

    }
}
