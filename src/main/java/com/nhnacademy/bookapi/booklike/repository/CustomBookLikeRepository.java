package com.nhnacademy.bookapi.booklike.repository;

import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;

import java.util.List;

public interface CustomBookLikeRepository {

    BookLikeResponse findBookLikeResponseById(Long id);

    List<BookLikeResponse> findBookLikesByBookId(Long bookId);

    List<BookLikeResponse> findBookLikesByUserId(String userId);
}
