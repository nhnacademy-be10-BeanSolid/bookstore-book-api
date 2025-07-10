package com.nhnacademy.bookapi.booktag.exception;


import com.nhnacademy.bookapi.common.exception.ConflictException;

public class BookTagAlreadyExistsException extends ConflictException {
    public BookTagAlreadyExistsException(String name) {
        super(String.format("BookTag '%s' already exists", name));
    }
}
