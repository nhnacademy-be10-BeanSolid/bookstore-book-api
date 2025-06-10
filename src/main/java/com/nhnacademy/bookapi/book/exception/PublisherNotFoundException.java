package com.nhnacademy.bookapi.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PublisherNotFoundException extends RuntimeException {

    private static final String MESSAGE = " 출판사는 도서를 출판하지 않았습니다.";

    public PublisherNotFoundException(String publisher) {
        super(publisher + MESSAGE);
    }
}
