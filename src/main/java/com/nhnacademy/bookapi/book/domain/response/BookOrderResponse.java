package com.nhnacademy.bookapi.book.domain.response;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookStatus;

public record BookOrderResponse (
    Long id,
    BookStatus status,
    int stock
)
{
    public static BookOrderResponse from(Book book){
        return new BookOrderResponse(
                book.getId(),
                book.getStatus(),
                book.getStock()
        );
    }
}
