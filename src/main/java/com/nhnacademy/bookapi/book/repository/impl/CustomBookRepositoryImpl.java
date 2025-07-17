package com.nhnacademy.bookapi.book.repository.impl;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.domain.QBook;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookOrderResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.repository.CustomBookRepository;
import com.nhnacademy.bookapi.bookcategory.domain.QBookCategory;
import com.nhnacademy.bookapi.booklike.domain.QBookLike;
import com.nhnacademy.bookapi.booktag.domain.QBookTag;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CustomBookRepositoryImpl implements CustomBookRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<BookResponse> findBookResponseById(Long id) {
        QBook book = QBook.book;

        Book result = queryFactory
                .selectFrom(book)
                .leftJoin(book.bookCategories).fetchJoin()
                .leftJoin(book.bookTags).fetchJoin()
                .where(book.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result).map(BookResponse::from);
    }

    @Override
    public Optional<BookDetailResponse> findBookDetailResponseByBookId(Long bookId) {
        QBook book = QBook.book;
        QBookTag tag = QBookTag.bookTag;
        QBookCategory category = QBookCategory.bookCategory;
        QBookLike like = QBookLike.bookLike;

        Book result = queryFactory
                .selectFrom(book)
                .leftJoin(book.bookTags, tag).fetchJoin()
                .leftJoin(book.bookCategories, category).fetchJoin()
                .where(book.id.eq(bookId))
                .distinct()
                .fetchOne();

        Long likeCount = queryFactory
                .select(like.count())
                .from(like)
                .where(like.book.id.eq(bookId))
                .fetchOne();

        return Optional.ofNullable(result)
                .map(r -> BookDetailResponse.from(r, likeCount != null ? likeCount.intValue() : 0));
    }

    @Override
    public int countBookCategoryByBookId(Long bookId) {
        QBook book = QBook.book;

        Book result = queryFactory
                .selectFrom(book)
                .leftJoin(book.bookCategories).fetchJoin()
                .where(book.id.eq(bookId))
                .fetchOne();

        return Optional.ofNullable(result)
                .map(Book::getBookCategories)
                .map(Set::size)
                .orElse(0);
    }

    // 주문 api 에서 필요한 정보
    @Override
    public List<BookOrderResponse> findBookOrderResponsesById(List<Long> ids) {
        QBook book = QBook.book;

        List<Book> results = queryFactory
                .selectFrom(book)
                .where(book.id.in(ids), book.status.eq(BookStatus.ON_SALE))
                .fetch();

        return results.stream()
                .map(BookOrderResponse::from)
                .toList();
    }

    // 정렬 조건
    private List<OrderSpecifier<?>> createOrderSpecifiers(Pageable pageable) {
        PathBuilder<Book> pathBuilder = new PathBuilder<>(Book.class, "book");

        // querydsl 에서 정렬에 사용하는 객체
        List<OrderSpecifier<?>> orderSpecifiers = pageable.getSort().stream()
                .map(order -> new OrderSpecifier<>(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        pathBuilder.getComparable(order.getProperty(), Comparable.class)
                ))
                .collect(Collectors.toList());

        // 보조 정렬 조건 추가 (id 내림차순)
        orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, pathBuilder.getComparable("id", Comparable.class)));

        return orderSpecifiers;
    }
}
