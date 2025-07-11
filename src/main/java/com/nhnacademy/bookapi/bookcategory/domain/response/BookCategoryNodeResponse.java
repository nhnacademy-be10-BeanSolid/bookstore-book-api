package com.nhnacademy.bookapi.bookcategory.domain.response;

import java.util.ArrayList;
import java.util.List;

public record BookCategoryNodeResponse(
        Long categoryId,
        String categoryName,
        List<BookCategoryNodeResponse> children
) {
    public static BookCategoryNodeResponse of(Long categoryId, String categoryName) {
        return new BookCategoryNodeResponse(categoryId, categoryName, new ArrayList<>());
    }
}
