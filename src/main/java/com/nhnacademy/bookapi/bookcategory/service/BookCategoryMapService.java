package com.nhnacademy.bookapi.bookcategory.service;

import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryMapCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;

public interface BookCategoryMapService {

    BookCategoryMapResponse bookCategoryMapCreate(Long bookId , BookCategoryMapCreateRequest request);

    void deleteCategory(Long bookId, Long categoryId);
}
