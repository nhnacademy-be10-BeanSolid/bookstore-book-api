package com.nhnacademy.bookapi.book.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookResponse(
        Long id,
        String title,
        String description,
        String toc,
        String publisher,
        String author,
        LocalDate publishedDate,
        String isbn,
        int originalPrice,
        int salePrice,
        Boolean wrappable,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updateAt,

        BookStatus status,
        int stock
) {
    public static BookResponse of(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getToc(),
                book.getPublisher(),
                book.getAuthor(),
                book.getPublishedDate(),
                book.getIsbn(),
                book.getOriginalPrice(),
                book.getSalePrice(),
                book.isWrappable(),
                book.getCreateAt(),
                book.getUpdateAt(),
                book.getStatus(),
                book.getStock()
        );
    }
}
