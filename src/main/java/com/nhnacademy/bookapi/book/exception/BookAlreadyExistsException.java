package com.nhnacademy.bookapi.book.exception;

import com.nhnacademy.bookapi.common.exception.ConflictException;

public class BookAlreadyExistsException extends ConflictException {
    public BookAlreadyExistsException(String isbn) {
        super(String.format("ISBN : %s already exists", isbn));
    }
}
