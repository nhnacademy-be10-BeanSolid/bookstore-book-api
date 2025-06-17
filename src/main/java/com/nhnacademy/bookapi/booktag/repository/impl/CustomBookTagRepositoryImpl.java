package com.nhnacademy.bookapi.booktag.repository.impl;

import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.domain.QBookTag;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
import com.nhnacademy.bookapi.booktag.repository.CustomBookTagRepository;
import com.querydsl.core.types.Projections;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;

public class CustomBookTagRepositoryImpl extends QuerydslRepositorySupport implements CustomBookTagRepository {
    public CustomBookTagRepositoryImpl() {
        super(BookTag.class);
    }

    @Override
    public Optional<BookTagResponse> findBookTagResponseById(Long id) {
        QBookTag bookTag = QBookTag.bookTag;

        BookTagResponse result = from(bookTag)
                .select(Projections.constructor(BookTagResponse.class,
                        bookTag.tagId,
                        bookTag.name
                ))
                .where(bookTag.tagId.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
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


}
