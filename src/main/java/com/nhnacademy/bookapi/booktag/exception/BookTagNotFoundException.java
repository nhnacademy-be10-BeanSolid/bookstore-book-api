package com.nhnacademy.bookapi.booktag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BookTagNotFoundException extends RuntimeException {
    public BookTagNotFoundException(Long tagId) {
        super("BookTag not found with id " + tagId);
    }

}
