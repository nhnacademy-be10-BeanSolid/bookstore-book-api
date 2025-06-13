package com.nhnacademy.bookapi.book.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

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

    private BookStatus status;
    private int stock;

    public static BookResponse of(Book book) {

        return new BookResponse(book.getId(),
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
                book.getStock());
    }

}
