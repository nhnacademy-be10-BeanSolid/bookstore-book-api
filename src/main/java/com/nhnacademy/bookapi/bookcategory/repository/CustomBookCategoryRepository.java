package com.nhnacademy.bookapi.bookcategory.repository;

import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;

import java.util.List;
import java.util.Optional;

public interface CustomBookCategoryRepository {

    Optional<BookCategoryResponse> findBookCategoryResponseById(Long id);

    List<BookCategoryResponse> findAllBookCategoryResponse();
}
