package com.nhnacademy.bookapi.book.repository.impl;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.QBook;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.repository.CustomBookRepository;
import com.nhnacademy.bookapi.bookcategory.domain.QBookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.booktag.domain.QBookTag;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;

public class CustomBookRepositoryImpl extends QuerydslRepositorySupport implements CustomBookRepository {

    private final JPAQueryFactory queryFactory;

    public CustomBookRepositoryImpl(EntityManager entityManager) {
        super(Book.class);
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Optional<BookResponse> findBookResponseById(Long id) {
        QBook book = QBook.book;

        Book result = from(book)
                .leftJoin(book.bookCategories).fetchJoin()
                .leftJoin(book.bookTags).fetchJoin()
                .where(book.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result).map(BookResponse::of);
    }

    @Override
    public Page<BookResponse> findBookResponsesByAuthor(String author, Pageable pageable) {
        QBook book = QBook.book;

        List<Book> books = queryFactory
                .selectFrom(book)
                .leftJoin(book.bookCategories).fetchJoin()
                .leftJoin(book.bookTags).fetchJoin()
                .where(book.author.eq(author))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(book.count())
                .from(book)
                .where(book.author.eq(author))
                .fetchOne();

        List<BookResponse> content = books.stream()
                .map(BookResponse::of)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<BookResponse> findBookDetailByPublisher(String publisher, Pageable pageable) {
        QBook book = QBook.book;

        List<Book> books = queryFactory
                .selectFrom(book)
                .leftJoin(book.bookCategories).fetchJoin()
                .leftJoin(book.bookTags).fetchJoin()
                .where(book.publisher.eq(publisher))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(book.count())
                .from(book)
                .where(book.publisher.eq(publisher))
                .fetchOne();

        List<BookResponse> content = books.stream()
                .map(BookResponse::of)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<BookTagMapResponse> findBookTagMapResponseByBookIdAndTagId(Long bookId, Long tagId) {
        QBook book = QBook.book;
        QBookTag tag = QBookTag.bookTag;

        return Optional.ofNullable(
                queryFactory
                        .select(Projections.constructor(BookTagMapResponse.class,
                                book.id,
                                tag.tagId
                        ))
                        .from(book)
                        .join(book.bookTags, tag)
                        .where(book.id.eq(bookId).and(tag.tagId.eq(tagId)))
                        .fetchOne()
        );
    }

    @Override
    public Optional<BookCategoryMapResponse> findBookCategoryMapResponseByBookIdAndCategoryId(Long bookId, Long categoryId) {
        QBook book = QBook.book;
        QBookCategory category = QBookCategory.bookCategory;

        return Optional.ofNullable(
                queryFactory.select(Projections.constructor(BookCategoryMapResponse.class,
                                book.id,
                                category.categoryId
                        ))
                        .from(book)
                        .join(book.bookCategories, category)
                        .where(book.id.eq(bookId).and(category.categoryId.eq(categoryId)))
                        .fetchOne()
        );
    }
}
