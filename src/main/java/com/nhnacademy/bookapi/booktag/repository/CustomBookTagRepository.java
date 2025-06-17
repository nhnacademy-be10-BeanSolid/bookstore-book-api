package com.nhnacademy.bookapi.booktag.repository;

import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;

import java.util.List;

public interface CustomBookTagRepository {

    BookTagResponse findBookTagResponseById(Long id);

    List<BookTagResponse> findAllBookTagResponses();

    BookTagMapResponse findBookTagMapResponseByBookId(Long bookId);
}
