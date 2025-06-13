package com.nhnacademy.bookapi.bookcategory.service;

import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryMapCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;

public interface BookCategoryMapService {

    BookCategoryMapResponse createBookCategoryMap(Long bookId , BookCategoryMapCreateRequest request);

    void deleteCategoryMap(Long bookId, Long categoryId);
}
