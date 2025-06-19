package com.nhnacademy.bookapi.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUserIdHeaderException extends RuntimeException {

    public InvalidUserIdHeaderException() {
        super("X-USER-ID is invalid");
    }
}
