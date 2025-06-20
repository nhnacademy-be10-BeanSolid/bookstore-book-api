package com.nhnacademy.bookapi.bookcategory.repository.impl;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.QBookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.repository.CustomBookCategoryRepository;
import com.querydsl.core.types.Projections;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;

public class CustomBookCategoryRepositoryImpl extends QuerydslRepositorySupport implements CustomBookCategoryRepository {
    public CustomBookCategoryRepositoryImpl() {
        super(BookCategory.class);
    }

    @Override
    public Optional<BookCategoryResponse> findBookCategoryResponseById(Long id) {
        QBookCategory bookCategory = QBookCategory.bookCategory;

        BookCategoryResponse result = from(bookCategory)
                .select(Projections.constructor(BookCategoryResponse.class,
                        bookCategory.categoryId,
                        bookCategory.parentCategory.categoryId,
                        bookCategory.name,
                        bookCategory.createdAt,
                        bookCategory.updatedAt
                ))
                .where(bookCategory.categoryId.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<BookCategoryResponse> findAllBookCategoryResponse() {
        QBookCategory bookCategory = QBookCategory.bookCategory;

        return from(bookCategory)
                .select(Projections.constructor(BookCategoryResponse.class,
                        bookCategory.categoryId,
                        bookCategory.parentCategory.categoryId,
                        bookCategory.name,
                        bookCategory.createdAt,
                        bookCategory.updatedAt
                ))
                .fetch();
    }
}
