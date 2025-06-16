package com.nhnacademy.bookapi.booktag.service;

import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagUpdateRequest;

import java.util.List;

public interface BookTagService {
    List<BookTag> getBookTags();

    BookTag getBookTag(Long tagId);

    BookTag createBookTag(BookTag bookTag);

    BookTag updateBookTag(BookTag bookTag);

    void deleteBookTag(Long tagId);

    boolean existsBookTag(Long tagId);

    boolean existsBookTag(String name);
}
