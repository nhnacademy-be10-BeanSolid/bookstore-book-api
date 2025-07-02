package com.nhnacademy.bookapi.book.exception;

import com.nhnacademy.bookapi.common.exception.BadRequestException;

public class InsufficientStockException extends BadRequestException {
    public InsufficientStockException(Long bookId) {
        super(String.format("Book ID %d has insufficient stock", bookId));
    }
}
