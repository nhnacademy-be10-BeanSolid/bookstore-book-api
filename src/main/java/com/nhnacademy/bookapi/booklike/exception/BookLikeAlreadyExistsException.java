package com.nhnacademy.bookapi.booklike.exception;

import com.nhnacademy.bookapi.common.exception.ConflictException;

public class BookLikeAlreadyExistsException extends ConflictException {

    public BookLikeAlreadyExistsException(String userId, String title) {
        super(String.format("User %s already liked to %s", userId, title));
    }
}
