package com.nhnacademy.bookapi.booktag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookTagMapAlreadyExistsException extends RuntimeException {
    public BookTagMapAlreadyExistsException(Long bookId, Long tagId) {
        super(String.format("Book id %s tag id %s already exists", bookId, tagId));
    }
}
