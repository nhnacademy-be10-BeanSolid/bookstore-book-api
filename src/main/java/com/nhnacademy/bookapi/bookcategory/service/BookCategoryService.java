package com.nhnacademy.bookapi.bookcategory.service;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;

import java.util.List;

public interface BookCategoryService {
    // 생성(Create)
    BookCategoryResponse createCategory(BookCategoryCreateRequest request);

    // 단건 조회(Read)
    BookCategoryResponse getCategoryById(Long categoryId);

    // 전체 조회(Read)
    List<BookCategoryResponse> getAllCategories();

    // 수정(Update)
    BookCategoryResponse updateCategory(Long categoryId, BookCategoryUpdateRequest request);

    // 삭제(Delete)
    void deleteCategory(Long categoryId);

    boolean existsCategory(String categoryName);

    boolean existsCategory(Long categoryId);
}
