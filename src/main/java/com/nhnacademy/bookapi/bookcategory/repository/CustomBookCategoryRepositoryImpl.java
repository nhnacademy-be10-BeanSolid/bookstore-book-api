package com.nhnacademy.bookapi.bookcategory.repository;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.QBookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.querydsl.core.types.Projections;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class CustomBookCategoryRepositoryImpl extends QuerydslRepositorySupport implements CustomBookCategoryRepository {
    public CustomBookCategoryRepositoryImpl() {
        super(BookCategory.class);
    }

    @Override
    public BookCategoryResponse findBookCategoryResponseById(Long id) {
        QBookCategory bookCategory = QBookCategory.bookCategory;

        return from(bookCategory)
                .select(Projections.constructor(BookCategoryResponse.class,
                        bookCategory.categoryId,
                        bookCategory.parentCategory.categoryId,
                        bookCategory.name,
                        bookCategory.createdAt,
                        bookCategory.updatedAt
                ))
                .where(bookCategory.categoryId.eq(id))
                .fetchOne();
    }
}
