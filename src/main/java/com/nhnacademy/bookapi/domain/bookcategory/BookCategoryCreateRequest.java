package com.nhnacademy.bookapi.domain.bookcategory;

import jakarta.validation.constraints.NotBlank;

public record BookCategoryCreateRequest (
        @NotBlank
        String name,
        Long parentId
)
{}
