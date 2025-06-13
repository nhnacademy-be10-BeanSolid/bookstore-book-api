package com.nhnacademy.bookapi.bookcategory.domain.request;

import jakarta.validation.constraints.NotBlank;

public record BookCategoryUpdateRequest (
        @NotBlank
        String name,
        Long parentId
)
{}
