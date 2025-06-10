package com.nhnacademy.bookapi.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationFailedException extends RuntimeException {

    private static final String MESSAGE = "유효한 값을 입력해주세요.";

    public ValidationFailedException() {
        super(MESSAGE);
    }
}
