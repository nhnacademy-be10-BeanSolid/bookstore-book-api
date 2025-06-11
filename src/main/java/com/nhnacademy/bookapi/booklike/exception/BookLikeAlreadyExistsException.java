package com.nhnacademy.bookapi.booklike.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookLikeAlreadyExistsException extends RuntimeException {

    public BookLikeAlreadyExistsException(String userId, String title) {
        super(String.format("User %s already liked to %s", userId, title));
    }
}
