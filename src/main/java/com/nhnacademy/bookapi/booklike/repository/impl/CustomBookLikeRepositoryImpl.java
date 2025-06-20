package com.nhnacademy.bookapi.booklike.repository.impl;

import com.nhnacademy.bookapi.booklike.domain.BookLike;
import com.nhnacademy.bookapi.booklike.domain.QBookLike;
import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import com.nhnacademy.bookapi.booklike.repository.CustomBookLikeRepository;
import com.querydsl.core.types.Projections;
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

    @Override
    public List<BookLikeResponse> findBookLikeResponsesByBookId(Long bookId) {
        QBookLike bookLike = QBookLike.bookLike;

        return from(bookLike)
                .select(Projections.constructor(BookLikeResponse.class,
                        bookLike.id,
                        bookLike.likedAt,
                        bookLike.userId,
                        bookLike.book.id
                ))
                .where(bookLike.book.id.eq(bookId))
                .fetch();
    }

    @Override
    public List<BookLikeResponse> findBookLikeResponsesByUserId(String userId) {
        QBookLike bookLike = QBookLike.bookLike;

        return from(bookLike)
                .select(Projections.constructor(BookLikeResponse.class,
                        bookLike.id,
                        bookLike.likedAt,
                        bookLike.userId,
                        bookLike.book.id
                ))
                .where(bookLike.userId.eq(userId))
                .fetch();
    }
}
