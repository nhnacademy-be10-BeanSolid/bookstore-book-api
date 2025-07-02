package com.nhnacademy.bookapi.booktag.exception;

import com.nhnacademy.bookapi.common.exception.NotFoundException;

public class BookTagNotFoundException extends NotFoundException {
    public BookTagNotFoundException(Long tagId) {
        super(String.format("BookTag not found with id %d", tagId));
    }
}
