package com.nhnacademy.bookapi.book.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;

import java.util.Optional;

public interface CustomBookRepository {

    Optional<BookResponse> findBookResponseById(Long id);

    Page<BookResponse> findBookResponsesByAuthor(String author, Pageable pageable);

    Page<BookResponse> findBookResponseByPublisher(String publisher, Pageable pageable);

    Optional<BookTagMapResponse> findBookTagMapResponseByBookIdAndTagId(Long bookId, Long tagId);

    Optional<BookCategoryMapResponse> findBookCategoryMapResponseByBookIdAndCategoryId(Long bookId, Long categoryId);

    int countBookCategoryByBookId(Long bookId);

    // 태그로 도서들 검색
    Page<BookResponse> findBookResponseByTag(String tag, Pageable pageable);
}
