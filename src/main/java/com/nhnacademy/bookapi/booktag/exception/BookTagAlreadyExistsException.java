package com.nhnacademy.bookapi.booktag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class BookTagAlreadyExistsException extends RuntimeException {
    public BookTagAlreadyExistsException(String name) {
        super("BookTag " + name + " already exists");
    }
}
