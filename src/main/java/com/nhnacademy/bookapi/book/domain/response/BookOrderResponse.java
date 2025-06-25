package com.nhnacademy.bookapi.book.domain.response;

import com.nhnacademy.bookapi.book.domain.Book;

public record BookOrderResponse (
    Long id,
    int price,
    int stock
)
{
    public static BookOrderResponse from(Book book){
        return new BookOrderResponse(
                book.getId(),
                book.getSalePrice(),
                book.getStock()
        );
    }
}
