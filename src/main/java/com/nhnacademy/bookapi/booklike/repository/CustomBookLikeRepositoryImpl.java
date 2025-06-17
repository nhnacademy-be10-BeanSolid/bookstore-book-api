package com.nhnacademy.bookapi.booklike.repository;

import com.nhnacademy.bookapi.booklike.domain.BookLike;
import com.nhnacademy.bookapi.booklike.domain.QBookLike;
import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import com.querydsl.core.types.Projections;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class CustomBookLikeRepositoryImpl extends QuerydslRepositorySupport implements CustomBookLikeRepository {

    public CustomBookLikeRepositoryImpl() {
        super(BookLike.class);
    }

    @Override
    public BookLikeResponse findBookLikeResponseById(Long id) {
        QBookLike bookLike = QBookLike.bookLike;

        return from(bookLike)
                .select(Projections.constructor(BookLikeResponse.class,
                        bookLike.id,
                        bookLike.likedAt,
                        bookLike.userId,
                        bookLike.book.id
                ))
                .where(bookLike.id.eq(id))
                .fetchOne();
    }

    @Override
    public List<BookLikeResponse> findBookLikesByBookId(Long bookId) {
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
    public List<BookLikeResponse> findBookLikesByUserId(String userId) {
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
