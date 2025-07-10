package com.nhnacademy.bookapi.booktag.exception;

import com.nhnacademy.bookapi.common.exception.ConflictException;

public class BookTagMapAlreadyExistsException extends ConflictException {
    public BookTagMapAlreadyExistsException(Long bookId, Long tagId) {
        super(String.format("Book id %s tag id %s already exists", bookId, tagId));
    }
}
