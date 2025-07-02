package com.nhnacademy.bookapi.bookcategory.exception;

import com.nhnacademy.bookapi.common.exception.NotFoundException;

public class BookCategoryNotFoundException extends NotFoundException {
    public BookCategoryNotFoundException(Long categoryId) {
        super("Book category with id " + categoryId + " not found.");
    }
}
