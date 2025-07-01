package com.nhnacademy.bookapi.book.domain.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookStockReduceRequest(
        @NotNull
        Long bookId,

        @Positive
        Integer stock
)
{}
