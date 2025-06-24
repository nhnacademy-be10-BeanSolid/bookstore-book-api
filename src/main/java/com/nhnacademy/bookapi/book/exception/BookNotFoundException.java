package com.nhnacademy.bookapi.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super(String.format("도서 아이디 %d에 해당하는 도서는 존재하지 않습니다.", id));
    }
}
