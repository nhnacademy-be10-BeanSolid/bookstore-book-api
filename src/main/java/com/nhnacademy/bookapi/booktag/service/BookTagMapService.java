package com.nhnacademy.bookapi.booktag.service;

import com.nhnacademy.bookapi.booktag.domain.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;

public interface BookTagMapService {

    BookTagMapResponse createBookTag(Long bookId ,BookTagMapCreateRequest request);

    void deleteBookTag(Long bookId, Long tagId);
}
