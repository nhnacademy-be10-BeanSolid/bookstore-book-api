package com.nhnacademy.bookapi.bookcategory.domain.response;

import java.util.List;

public record BookCategoryMapResponse(
        Long bookId,
        List<BookCategoryResponse> categories
){
}