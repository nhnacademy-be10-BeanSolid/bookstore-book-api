package com.nhnacademy.bookapi.booktag.exception;

import com.nhnacademy.bookapi.common.exception.NotFoundException;

public class BookTagMapNotFoundException extends NotFoundException {
    public BookTagMapNotFoundException(Long bookId, Long tagId) {
        super(String.format("Book id %s tag id %s not found", bookId, tagId));
    }
}
