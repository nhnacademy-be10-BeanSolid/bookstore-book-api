package com.nhnacademy.bookapi.book.domain.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record BookUpdateRequest (
        @NotBlank
        @Size(max=255)
        String title,

        @NotBlank
        String description,

        @NotBlank
        String toc,

        @NotBlank
        @Size(max=255)
        String publisher,

        @NotBlank
        @Size(max=255)
        String author,

        @NotNull
        LocalDate publishedDate,

        @NotNull
        @Positive
        Integer originalPrice,

        @NotNull
        @Positive
        Integer salePrice,

        @NotNull
        Boolean wrappable,

        @NotBlank
        String status,

        @NotNull
        @PositiveOrZero
        Integer stock
){}
