package com.nhnacademy.bookapi.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyExistsException extends RuntimeException {

    public BookAlreadyExistsException(String isbn) {
        super(String.format("ISBN 코드 %s에 해당하는 도서는 이미 존재합니다.", isbn));
    }
}
