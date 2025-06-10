package com.nhnacademy.bookapi.domain.bookcategory;

import jakarta.validation.constraints.NotBlank;

public record BookCategoryUpdateRequest (
        @NotBlank
        String name,
        Long parentId
)
{}
