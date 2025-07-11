package com.nhnacademy.bookapi.bookcategory.domain.response;

import java.time.LocalDateTime;

public record BookCategoryResponse (
        Long categoryId,
        String categoryName,
        Long parentId,
        String parentCategoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}