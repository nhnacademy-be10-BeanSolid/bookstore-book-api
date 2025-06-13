package com.nhnacademy.bookapi.booktag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookTagMapNotFoundException extends RuntimeException {
    public BookTagMapNotFoundException(Long bookId, Long tagId) {
        super(String.format("Book id %s tag id %s not found", bookId, tagId));
    }
}
