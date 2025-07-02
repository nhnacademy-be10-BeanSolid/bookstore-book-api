package com.nhnacademy.bookapi.common.exception;

public class InvalidHeaderException extends BadRequestException {
    public InvalidHeaderException() {
        super("Invalid user ID");
    }
}
