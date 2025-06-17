package com.nhnacademy.bookapi.booktag.repository.impl;

import com.nhnacademy.bookapi.book.domain.QBook;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.domain.QBookTag;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
import com.nhnacademy.bookapi.booktag.repository.CustomBookTagRepository;
import com.querydsl.core.types.Projections;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class CustomBookTagRepositoryImpl extends QuerydslRepositorySupport implements CustomBookTagRepository {
    public CustomBookTagRepositoryImpl() {
        super(BookTag.class);
    }

    @Override
    public BookTagResponse findBookTagResponseById(Long id) {
        QBookTag bookTag = QBookTag.bookTag;

        return from(bookTag)
                .select(Projections.constructor(BookTagResponse.class,
                        bookTag.tagId,
                        bookTag.name
                ))
                .where(bookTag.tagId.eq(id))
                .fetchOne();
    }

    @Override
    public List<BookTagResponse> findAllBookTagResponses() {
        QBookTag bookTag = QBookTag.bookTag;

        return from(bookTag)
                .select(Projections.constructor(BookTagResponse.class,
                        bookTag.tagId,
                        bookTag.name
                ))
                .fetch();
    }

    // book쪽으로 옮기기
    @Override
    public BookTagMapResponse findBookTagMapResponseByBookId(Long bookId) {
        QBook book = QBook.book;
        QBookTag bookTag = QBookTag.bookTag;

        return from(book)
                .join(book.bookTags, bookTag)
                .where(book.id.eq(bookId))
                .select(Projections.constructor(BookTagMapResponse.class,
                        book.id,
                        bookTag.tagId))
                .fetchOne();
    }

}
