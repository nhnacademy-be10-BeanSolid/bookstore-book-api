package com.nhnacademy.bookapi.book.controller.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {

    @Size(max=255)
    private String title;

    private String description;

    private String toc;

    @Size(max=255)
    private String publisher;

    @Size(max=255)
    private String author;

    private LocalDate publishedDate;

    @Pattern(regexp = "^.{13}$")
    private String isbn;

    @Positive
    private Integer originalPrice;

    @Positive
    private Integer salePrice;

    private Boolean wrappable;

    private String status;

    @PositiveOrZero
    private Integer stock;
}
