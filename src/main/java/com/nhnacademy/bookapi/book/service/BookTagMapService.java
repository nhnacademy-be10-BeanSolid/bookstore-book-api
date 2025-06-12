package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.controller.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.book.controller.response.BookTagMapResponse;

import java.util.List;

public interface BookTagMapService {

    BookTagMapResponse createBookTagMap(BookTagMapCreateRequest request);

    List<BookTagMapResponse> findBookTagMapByBookId(Long bookId);

    void deleteBookTagMap(Long bookId, Long tagId);
}
