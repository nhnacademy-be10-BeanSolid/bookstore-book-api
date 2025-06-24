package com.nhnacademy.bookapi.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Set;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookNotSaleException extends RuntimeException {

    public BookNotSaleException(Set<Long> ids) {
        super(String.format("다음 도서 아이디는 현재 판매하지 않습니다: %s",
                ids.toString()));
    }
}
