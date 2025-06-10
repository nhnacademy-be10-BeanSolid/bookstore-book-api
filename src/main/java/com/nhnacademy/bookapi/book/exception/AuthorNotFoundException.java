package com.nhnacademy.bookapi.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AuthorNotFoundException extends RuntimeException {

    private static final String MESSAGE = " 작가는 책을 집필하지 않았습니다.";

    public AuthorNotFoundException(String author) {
        super(author + MESSAGE);
    }
}
