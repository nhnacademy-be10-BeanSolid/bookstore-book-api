package com.nhnacademy.bookapi.book.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.booktag.domain.BookTag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record BookDetailResponse (

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
    int stock,

    Set<String> bookCategories,

    Set<String> bookTags
) {
public static BookDetailResponse of(Book book) {
    Set<String> categories = book.getBookCategories()
            .stream()
            .map(BookCategory::getName)
            .collect(Collectors.toSet());

    Set<String> tags = book.getBookTags()
            .stream()
            .map(BookTag::getName)
            .collect(Collectors.toSet());

    return new BookDetailResponse(
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
            book.getStock(),
            categories,
            tags);
    }
}

