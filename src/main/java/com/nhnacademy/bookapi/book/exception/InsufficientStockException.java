package com.nhnacademy.bookapi.book.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long bookId) {
        super(String.format("아이디 %d에 해당하는 도서는 재고가 부족합니다.", bookId));
    }
}
