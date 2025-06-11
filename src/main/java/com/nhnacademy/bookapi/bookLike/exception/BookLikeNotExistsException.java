package com.nhnacademy.bookapi.bookLike.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookLikeNotExistsException extends RuntimeException {

    public BookLikeNotExistsException(String userId, Long bookId) {
        super(String.format("User '%s' has not liked the book with ID %d.", userId, bookId));
    }

    public BookLikeNotExistsException(Long bookId) {
        super(String.format("No user has liked the book with ID %d.", bookId));
    }
}
