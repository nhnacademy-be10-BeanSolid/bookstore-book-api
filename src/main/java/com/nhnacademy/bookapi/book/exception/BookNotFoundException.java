package com.nhnacademy.bookapi.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends RuntimeException {

    private static final String MESSAGE = "인 ISBN 코드에 해당하는 도서는 존재하지 않습니다.";

    public BookNotFoundException(String isbn) {
        super(isbn + MESSAGE);
    }

    public BookNotFoundException(Long id) {
        super(id + MESSAGE);
    }
}
