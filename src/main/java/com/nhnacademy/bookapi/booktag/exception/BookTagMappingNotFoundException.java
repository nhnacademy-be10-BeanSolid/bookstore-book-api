package com.nhnacademy.bookapi.booktag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookTagMappingNotFoundException extends RuntimeException {
    public BookTagMappingNotFoundException(Long bookId, Long tagId) {
        super();
    }
}
