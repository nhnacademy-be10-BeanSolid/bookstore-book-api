package com.nhnacademy.bookapi.book.repository;


import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;

import java.util.Optional;

public interface CustomBookRepository {

    Optional<BookResponse> findBookResponseById(Long id);
    // 도서 상세 정보
    Optional<BookDetailResponse> findBookDetailResponseByBookId(Long bookId);

    Page<BookResponse> findBookResponsesByAuthor(String author, Pageable pageable);

    Page<BookResponse> findBookResponseByPublisher(String publisher, Pageable pageable);

    // 태그로 도서들 검색
    Page<BookResponse> findBookResponseByTag(String tag, Pageable pageable);

    // 도서 이름으로 검색
    Page<BookResponse> findBookResponseByTitle(String title, Pageable pageable);

    Optional<BookTagMapResponse> findBookTagMapResponseByBookIdAndTagId(Long bookId, Long tagId);

    Optional<BookCategoryMapResponse> findBookCategoryMapResponseByBookIdAndCategoryId(Long bookId, Long categoryId);

    int countBookCategoryByBookId(Long bookId);
}
