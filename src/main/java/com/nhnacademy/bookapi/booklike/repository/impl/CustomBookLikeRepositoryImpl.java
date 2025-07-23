package com.nhnacademy.bookapi.booklike.repository.impl;

import com.nhnacademy.bookapi.booklike.domain.QBookLike;
import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import com.nhnacademy.bookapi.booklike.repository.CustomBookLikeRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomBookLikeRepositoryImpl implements CustomBookLikeRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<BookLikeResponse> findBookLikeResponseById(Long id) {
        QBookLike bookLike = QBookLike.bookLike;

        BookLikeResponse result = queryFactory
                .select(Projections.constructor(BookLikeResponse.class,
                        bookLike.id,
                        bookLike.likedAt,
                        bookLike.userId,
                        bookLike.book.id,
                        bookLike.book.title
                ))
                .from(bookLike)
                .where(bookLike.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    // 도서
    @Override
    public Page<BookLikeResponse> findBookLikeResponsesByBookId(Long bookId, Pageable pageable) {
        QBookLike bookLike = QBookLike.bookLike;

        List<BookLikeResponse> content = queryFactory
                .select(Projections.constructor(BookLikeResponse.class,
                        bookLike.id,
                        bookLike.likedAt,
                        bookLike.userId,
                        bookLike.book.id,
                        bookLike.book.title
                ))
                .from(bookLike)
                .where(bookLike.book.id.eq(bookId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(bookLike.count())
                .from(bookLike)
                .where(bookLike.book.id.eq(bookId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    // 마이페이지
    @Override
    public Page<BookLikeResponse> findBookLikeResponsesByUserId(String userId, Pageable pageable) {
        QBookLike bookLike = QBookLike.bookLike;

        List<BookLikeResponse> content = queryFactory
                .select(Projections.constructor(BookLikeResponse.class,
                        bookLike.id,
                        bookLike.likedAt,
                        bookLike.userId,
                        bookLike.book.id,
                        bookLike.book.title
                ))
                .from(bookLike)
                .where(bookLike.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(bookLike.count())
                .from(bookLike)
                .where(bookLike.userId.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
