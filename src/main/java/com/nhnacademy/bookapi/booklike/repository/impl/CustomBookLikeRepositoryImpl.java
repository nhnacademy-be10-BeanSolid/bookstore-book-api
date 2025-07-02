package com.nhnacademy.bookapi.booklike.repository.impl;

import com.nhnacademy.bookapi.booklike.domain.BookLike;
import com.nhnacademy.bookapi.booklike.domain.QBookLike;
import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import com.nhnacademy.bookapi.booklike.repository.CustomBookLikeRepository;
import com.querydsl.core.types.Projections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;

public class CustomBookLikeRepositoryImpl extends QuerydslRepositorySupport implements CustomBookLikeRepository {

    public CustomBookLikeRepositoryImpl() {
        super(BookLike.class);
    }

    @Override
    public Optional<BookLikeResponse> findBookLikeResponseById(Long id) {
        QBookLike bookLike = QBookLike.bookLike;

        BookLikeResponse result = from(bookLike)
                .select(Projections.constructor(BookLikeResponse.class,
                        bookLike.id,
                        bookLike.likedAt,
                        bookLike.userId,
                        bookLike.book.id
                ))
                .where(bookLike.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    // 도서
    @Override
    public Page<BookLikeResponse> findBookLikeResponsesByBookId(Long bookId, Pageable pageable) {
        QBookLike bookLike = QBookLike.bookLike;

        List<BookLikeResponse> content = from(bookLike)
                .select(Projections.constructor(BookLikeResponse.class,
                        bookLike.id,
                        bookLike.likedAt,
                        bookLike.userId,
                        bookLike.book.id
                ))
                .where(bookLike.book.id.eq(bookId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = from(bookLike)
                .select(bookLike.count())
                .where(bookLike.book.id.eq(bookId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    // 마이페이지
    @Override
    public Page<BookLikeResponse> findBookLikeResponsesByUserId(String userId, Pageable pageable) {
        QBookLike bookLike = QBookLike.bookLike;

        List<BookLikeResponse> content = from(bookLike)
                .select(Projections.constructor(BookLikeResponse.class,
                        bookLike.id,
                        bookLike.likedAt,
                        bookLike.userId,
                        bookLike.book.id
                ))
                .where(bookLike.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = from(bookLike)
                .select(bookLike.count())
                .where(bookLike.userId.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

}
