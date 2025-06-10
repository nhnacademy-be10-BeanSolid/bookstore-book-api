package com.nhnacademy.bookapi.bookcategory.domain;

import jakarta.validation.constraints.NotBlank;

public record BookCategoryCreateRequest (
        @NotBlank
        String name,
        Long parentId
)
{}
