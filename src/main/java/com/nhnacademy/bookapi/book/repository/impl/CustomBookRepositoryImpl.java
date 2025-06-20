package com.nhnacademy.bookapi.book.repository.impl;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.QBook;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.repository.CustomBookRepository;
import com.nhnacademy.bookapi.bookcategory.domain.QBookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.booklike.domain.QBookLike;
import com.nhnacademy.bookapi.booktag.domain.QBookTag;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class CustomBookRepositoryImpl extends QuerydslRepositorySupport implements CustomBookRepository {

    private final JPAQueryFactory queryFactory;

    public CustomBookRepositoryImpl(EntityManager entityManager) {
        super(Book.class);
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

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
        QBookLike likes = QBookLike.bookLike;

        Book result = queryFactory
                .selectFrom(book)
                .leftJoin(book.bookTags, tag).fetchJoin()
                .leftJoin(book.bookCategories, category).fetchJoin()
                .leftJoin(book.bookLikes, likes).fetchJoin()
                .where(book.id.eq(bookId))
                .distinct()
                .fetchOne();

        return Optional.ofNullable(result).map(BookDetailResponse::from);
    }

    @Override
    public Page<BookResponse> findAllBookResponses(Pageable pageable) {
        QBook book = QBook.book;

        List<Book> books = queryFactory
                .selectFrom(book)
                .orderBy(book.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<BookResponse> content = books.stream()
                .map(BookResponse::from)
                .toList();

        Long total = Optional.ofNullable(
                queryFactory
                        .select(book.count())
                        .from(book)
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
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

        List<BookResponse> content = books.stream()
                .map(BookResponse::from)
                .toList();

        Long total = Optional.ofNullable(
                queryFactory
                    .select(book.count())
                    .from(book)
                    .where(book.author.eq(author))
                    .fetchOne())
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<BookResponse> findBookResponseByPublisher(String publisher, Pageable pageable) {
        QBook book = QBook.book;

        List<Book> books = queryFactory
                .selectFrom(book)
                .leftJoin(book.bookCategories).fetchJoin()
                .leftJoin(book.bookTags).fetchJoin()
                .where(book.publisher.eq(publisher))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<BookResponse> content = books.stream()
                .map(BookResponse::from)
                .toList();

        Long total = Optional.ofNullable(
                queryFactory
                    .select(book.count())
                    .from(book)
                    .where(book.publisher.eq(publisher))
                    .fetchOne())
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<BookResponse> findBookResponseByTag(String tag, Pageable pageable) {
        QBook book = QBook.book;
        QBookTag tags = QBookTag.bookTag;

        if (tag == null || tag.isBlank()) {
            return Page.empty(pageable);  // 빈 페이지 반환
        }

        List<Book> books = queryFactory
                .selectFrom(book)
                .join(book.bookTags, tags).fetchJoin()
                .leftJoin(book.bookCategories).fetchJoin()
                .where(tags.name.eq(tag))
                .fetch();

        log.info("books: {}", books);

        List<BookResponse> content = books.stream()
                .map(BookResponse::from)
                .toList();

        Long total = Optional.ofNullable(
                queryFactory
                    .select(book.count())
                    .from(book)
                    .join(book.bookTags, tags)
                    .where(tags.name.eq(tag))
                    .fetchOne())
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    // 도서 이름으로 검색
    @Override
    public Page<BookResponse> findBookResponseByTitle(String title, Pageable pageable) {
        QBook book = QBook.book;

        if(title == null || title.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Book> books = queryFactory
                .selectFrom(book)
                .where(book.title.containsIgnoreCase(title))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<BookResponse> content = books.stream()
                .map(BookResponse::from)
                .toList();

        Long total = Optional.ofNullable(
                queryFactory
                    .select(book.count())
                    .from(book)
                    .where(book.title.containsIgnoreCase(title))
                    .fetchOne())
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    // 도서 설명으로 검색
    @Override
    public Page<BookResponse> findBookResponseByDescription(String description, Pageable pageable) {
        QBook book = QBook.book;

        if (description == null || description.isBlank()) {
            return Page.empty(pageable);  // 빈 페이지 반환
        }

        List<Book> books = queryFactory
                .selectFrom(book)
                .where(book.description.containsIgnoreCase(description))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<BookResponse> content = books.stream()
                .map(BookResponse::from)
                .toList();

        Long total = Optional.ofNullable(
                queryFactory
                    .select(book.count())
                    .from(book)
                    .where(book.description.containsIgnoreCase(description))
                    .fetchOne())
                .orElse(0L);

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
}
