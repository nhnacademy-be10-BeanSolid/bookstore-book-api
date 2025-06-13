package com.nhnacademy.bookapi.bookcategory.domain.request;

import jakarta.validation.constraints.NotNull;

public record BookCategoryMapCreateRequest (
        @NotNull
        Long categoryId
)
{}
