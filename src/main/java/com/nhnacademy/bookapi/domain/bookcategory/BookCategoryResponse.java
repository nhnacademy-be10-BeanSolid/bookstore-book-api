package com.nhnacademy.bookapi.domain.bookcategory;

import java.time.LocalDateTime;

public record BookCategoryResponse (
        Long categoryId,
        Long parentId,
        String categoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
)
{}
