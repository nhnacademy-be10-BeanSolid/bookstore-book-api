package com.nhnacademy.bookapi.booktag.service;

import com.nhnacademy.bookapi.booktag.domain.request.BookTagCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagUpdateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;

import java.util.List;

public interface BookTagService {
    List<BookTagResponse> getBookTags();

    BookTagResponse getBookTag(Long tagId);

    BookTagResponse createBookTag(BookTagCreateRequest request);

    BookTagResponse updateBookTag(Long tagId, BookTagUpdateRequest request);

    void deleteBookTag(Long tagId);

    boolean existsBookTag(Long tagId);

    boolean existsBookTag(String name);
}
