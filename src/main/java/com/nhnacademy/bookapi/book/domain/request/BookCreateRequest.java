package com.nhnacademy.bookapi.book.domain.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record BookCreateRequest(

        @NotBlank
        @Size(max = 255)
        String title,

        String description,

        String toc,

        @NotBlank
        @Size(max = 255)
        String publisher,

        @NotBlank
        @Size(max = 255)
        String author,

        @NotNull
        LocalDate publishedDate,

        @NotNull
        @Pattern(regexp = "^.{13}$")
        String isbn,

        @Positive
        Integer originalPrice,

        @Positive
        Integer salePrice,

        @NotNull
        Boolean wrappable,

        @PositiveOrZero
        Integer stock
) {}
