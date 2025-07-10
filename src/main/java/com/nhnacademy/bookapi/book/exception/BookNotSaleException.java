package com.nhnacademy.bookapi.book.exception;

import com.nhnacademy.bookapi.common.exception.BadRequestException;

import java.util.Set;

public class BookNotSaleException extends BadRequestException {
    public BookNotSaleException(Set<Long> ids) {
        super(String.format("Not sale with Book ID: %s",
                ids.toString()));
    }
}
