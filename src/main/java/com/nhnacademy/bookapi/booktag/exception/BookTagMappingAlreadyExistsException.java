package com.nhnacademy.bookapi.booktag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookTagMappingAlreadyExistsException extends RuntimeException {
    public BookTagMappingAlreadyExistsException(Long bookId, Long tagId) {
        super();
    }
}
