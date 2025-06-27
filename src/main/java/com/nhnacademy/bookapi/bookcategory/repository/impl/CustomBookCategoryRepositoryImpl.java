package com.nhnacademy.bookapi.bookcategory.repository.impl;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.QBookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.repository.CustomBookCategoryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomBookCategoryRepositoryImpl implements CustomBookCategoryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<BookCategoryResponse> findBookCategoryResponseById(Long id) {
        QBookCategory bookCategory = QBookCategory.bookCategory;
        QBookCategory parent = new QBookCategory("parent");

        BookCategoryResponse result = queryFactory
                .select(Projections.constructor(BookCategoryResponse.class,
                        bookCategory.categoryId,
//                        bookCategory.parentCategory.categoryId,
                        bookCategory.name,
                        parent.name,
                        bookCategory.createdAt,
                        bookCategory.updatedAt
                ))
                .from(bookCategory)
                .leftJoin(bookCategory.parentCategory, parent)
                .where(bookCategory.categoryId.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<BookCategoryResponse> findAllBookCategoryResponse(Pageable pageable) {
        QBookCategory bookCategory = QBookCategory.bookCategory;
        QBookCategory parent = new QBookCategory("parent");

        List<BookCategoryResponse> result = queryFactory
                .select(Projections.constructor(BookCategoryResponse.class,
                        bookCategory.categoryId,
                        bookCategory.name,
                        parent.name,
                        bookCategory.createdAt,
                        bookCategory.updatedAt
                ))
                .from(bookCategory)
                .leftJoin(bookCategory.parentCategory, parent)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(bookCategory.count())
                .from(bookCategory)
                .fetchOne();

        return new PageImpl<>(result, pageable, total != null ? total : 0);
    }
}
