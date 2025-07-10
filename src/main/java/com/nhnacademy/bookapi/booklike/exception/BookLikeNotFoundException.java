package com.nhnacademy.bookapi.booklike.exception;

import com.nhnacademy.bookapi.common.exception.NotFoundException;

public class BookLikeNotFoundException extends NotFoundException {

    public BookLikeNotFoundException(String userId, Long bookId) {
        super(String.format("User '%s' has not liked the book with ID %d.", userId, bookId));
    }

    public BookLikeNotFoundException(Long bookId) {
        super(String.format("No user has liked the book with ID %d.", bookId));
    }
}
