package com.nhnacademy.bookapi.bookcategory.exception;

import com.nhnacademy.bookapi.common.exception.BadRequestException;

public class BookCategoryMapDeleteFailException extends BadRequestException {
    public BookCategoryMapDeleteFailException() {
        super("At least one category must be associated.");
    }
}
