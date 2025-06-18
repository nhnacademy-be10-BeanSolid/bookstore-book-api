package com.nhnacademy.bookapi.booklike.service;

import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;

import java.util.List;

public interface BookLikeService {

    BookLikeResponse createBookLike(Long bookId, String userId);

    List<BookLikeResponse> getBookLikesByUserId(String userId);

    List<BookLikeResponse> getBookLikesByBookId(Long bookId);

    void deleteBookLikeByUserIdAndBookId(String userId, Long bookId);

    void deleteBookLikeByBookId(Long bookId);
}
