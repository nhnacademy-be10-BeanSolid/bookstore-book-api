package com.nhnacademy.bookapi.bookcategory.service;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;

import java.util.List;

public interface BookCategoryService {
    // 생성(Create)
    BookCategory createCategory(BookCategory bookCategory);

    // 단건 조회(Read)
    BookCategory getCategoryById(Long categoryId);

    // 전체 조회(Read)
    List<BookCategory> getAllCategories();

    // 수정(Update)
    BookCategory updateCategory(Long categoryId, BookCategory bookCategory);

    // 삭제(Delete)
    void deleteCategory(Long categoryId);

    boolean existsCategory(String categoryName);

    boolean existsCategory(Long categoryId);
}
