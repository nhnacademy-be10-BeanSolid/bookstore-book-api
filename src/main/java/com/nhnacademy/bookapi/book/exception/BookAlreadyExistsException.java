package com.nhnacademy.bookapi.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE = "인 ISBN 코드에 해당하는 도서는 이미 존재합니다.";

    public BookAlreadyExistsException(String isbn) {
        super(isbn + MESSAGE);
    }
}
