package com.nhnacademy.bookapi.book.domain.response;

import com.nhnacademy.bookapi.book.domain.Book;

public record BookOrderResponse (
    Long id,
    String title,
    int stock
)
{
    public static BookOrderResponse from(Book book){
        return new BookOrderResponse(
                book.getId(),
                book.getTitle(),
                book.getStock()
        );
    }
}
