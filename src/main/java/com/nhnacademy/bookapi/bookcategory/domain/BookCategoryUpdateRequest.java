package com.nhnacademy.bookapi.bookcategory.domain;

import jakarta.validation.constraints.NotBlank;

public record BookCategoryUpdateRequest (
        @NotBlank
        String name,
        Long parentId
)
{}
