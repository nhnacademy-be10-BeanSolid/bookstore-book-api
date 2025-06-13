package com.nhnacademy.bookapi.bookcategory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookCategoryMapAlreadyExistsException extends RuntimeException {

    public BookCategoryMapAlreadyExistsException(Long bookId, Long categoryId) {
        super(String.format("Book id %s category id %s already exists", bookId, categoryId));
    }

}
