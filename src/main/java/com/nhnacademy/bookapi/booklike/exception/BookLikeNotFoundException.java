package com.nhnacademy.bookapi.booklike.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookLikeNotFoundException extends RuntimeException {

    public BookLikeNotFoundException(String userId, Long bookId) {
        super(String.format("User '%s' has not liked the book with ID %d.", userId, bookId));
    }

    public BookLikeNotFoundException(Long bookId) {
        super(String.format("No user has liked the book with ID %d.", bookId));
    }
}
