package com.nhnacademy.bookapi.book.repository;

import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;

import java.util.List;
import java.util.Optional;

public interface CustomBookRepository {

    Optional<BookResponse> findBookResponseById(Long id);

    List<BookResponse> findBookResponsesByAuthor(String author);

    List<BookResponse> findBookDetailByPublisher(String publisher);

    Optional<BookTagMapResponse> findBookTagMapResponseByBookIdAndTagId(Long bookId, Long tagId);

    Optional<BookCategoryMapResponse> findBookCategoryMapResponseByBookIdAndCategoryId(Long bookId, Long categoryId);

}
