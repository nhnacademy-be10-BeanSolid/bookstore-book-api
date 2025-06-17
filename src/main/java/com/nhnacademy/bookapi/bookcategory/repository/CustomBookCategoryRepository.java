package com.nhnacademy.bookapi.bookcategory.repository;

import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;

public interface CustomBookCategoryRepository {

    BookCategoryResponse findBookCategoryResponseById(Long id);
}
