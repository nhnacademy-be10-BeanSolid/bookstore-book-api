package com.nhnacademy.bookapi.book.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailResponse {

    private Long id;
    private String title;
    private String description;
    private String toc;
    private String publisher;
    private String author;
    private LocalDate publishedDate;
    private String isbn;
    private int originalPrice;
    private int salePrice;
    private Boolean wrappable;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    private BookStatus Status;
    private int stock;

    private Set<String> bookTags;

    public static BookDetailResponse of(Book book) {
        Set<String> tags = book.getBookTags()
                .stream()
                .map(BookTag::getName)
                .collect(Collectors.toSet());

        return new BookDetailResponse(book.getId(),
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
                tags);
    }

}

