package com.nhnacademy.bookapi.booktag.repository.impl;

import com.nhnacademy.bookapi.book.domain.QBook;
import com.nhnacademy.bookapi.booktag.domain.QBookTag;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
import com.nhnacademy.bookapi.booktag.repository.CustomBookTagRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomBookTagRepositoryImpl implements CustomBookTagRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<BookTagResponse> findBookTagResponseById(Long id) {
        QBookTag bookTag = QBookTag.bookTag;

        if(id == null) {
            return Optional.empty();
        }

        BookTagResponse result = queryFactory
                .select(Projections.constructor(BookTagResponse.class,
                        bookTag.tagId,
                        bookTag.name
                ))
                .from(bookTag)
                .where(bookTag.tagId.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<BookTagResponse> findAllBookTagResponses(Pageable pageable) {
        QBookTag bookTag = QBookTag.bookTag;

        List<BookTagResponse> result = queryFactory
                .select(Projections.constructor(BookTagResponse.class,
                        bookTag.tagId,
                        bookTag.name
                ))
                .from(bookTag)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(bookTag.count())
                .from(bookTag)
                .fetchOne();

        return new PageImpl<>(result, pageable, total != null ? total : 0);
    }

    @Override
    public BookTagMapResponse findBookTagMapResponse(Long bookId) {
        QBook book = QBook.book;
        QBookTag tag = QBookTag.bookTag;

        List<BookTagResponse> result = queryFactory
                .select(Projections.constructor(BookTagResponse.class,
                        tag.tagId,
                        tag.name
                ))
                .from(book)
                .join(book.bookTags, tag)
                .where(book.id.eq(bookId))
                .fetch();

        return new BookTagMapResponse(bookId, result);
    }
}
