package com.nhnacademy.bookapi.book.domain.response;

import com.nhnacademy.bookapi.book.domain.Book;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookOrderResponse (
        Long id,
        String title,
        String description,
        String toc,
        String author,
        String publisher,
        LocalDate publishedDate,
        String isbn,
        int originalPrice,
        int salePrice,
        boolean wrappable,
        LocalDateTime createAt,
        LocalDateTime updateAt,
        String status,
        int stock
)
{
    public static BookOrderResponse from(Book book){
        return new BookOrderResponse(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getToc(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishedDate(),
                book.getIsbn(),
                book.getOriginalPrice(),
                book.getSalePrice(),
                book.isWrappable(),
                book.getCreateAt(),
                book.getUpdateAt(),
                book.getStatus().name(),
                book.getStock()
        );
    }
}
