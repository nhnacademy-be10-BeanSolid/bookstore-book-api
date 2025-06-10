package com.nhnacademy.bookapi.book.domain;

public enum BookStatus {
    ON_SALE,
    SALE_END;

    public static BookStatus from(String value) {
        return BookStatus.valueOf(value.toUpperCase());
    }
}
