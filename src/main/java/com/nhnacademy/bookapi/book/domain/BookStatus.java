package com.nhnacademy.bookapi.book.domain;

import lombok.Getter;

@Getter
public enum BookStatus {
    ON_SALE("판매중"),
    SALE_END("판매종료");

    private final String label;

    BookStatus(String label) {
        this.label = label;
    }

    public static BookStatus from(String value) {
        return BookStatus.valueOf(value.toUpperCase());
    }
}
