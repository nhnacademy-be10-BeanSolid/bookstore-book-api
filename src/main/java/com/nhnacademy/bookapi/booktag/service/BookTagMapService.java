package com.nhnacademy.bookapi.booktag.service;

import com.nhnacademy.bookapi.booktag.controller.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.booktag.controller.response.BookTagMapResponse;

public interface BookTagMapService {

    BookTagMapResponse createBookTag(Long bookId ,BookTagMapCreateRequest request);

    void deleteBookTag(Long bookId, Long tagId);
}
