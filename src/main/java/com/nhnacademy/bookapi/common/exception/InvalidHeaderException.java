package com.nhnacademy.bookapi.common.exception;

public class InvalidHeaderException extends BadRequestException {
    public static final String USER_ID_BLANK = "User ID must not be blank";
    public static final String USER_ID_TOO_LONG = "User ID must not exceed 20 characters";

    public InvalidHeaderException(String message) {
        super(message);
    }
}
