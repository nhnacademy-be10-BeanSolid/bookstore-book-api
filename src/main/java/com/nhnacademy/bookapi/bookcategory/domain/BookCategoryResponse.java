package com.nhnacademy.bookapi.bookcategory.domain;

import java.time.LocalDateTime;

public record BookCategoryResponse (
        Long categoryId,
        Long parentId,
        String categoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
)
{}
