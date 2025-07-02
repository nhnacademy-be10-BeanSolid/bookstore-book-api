package com.nhnacademy.bookapi.common.exception;

public class ConflictException extends CustomHttpException {
    public ConflictException(String message) {
        super(CustomHttpStatus.CONFLICT, message);
    }
}