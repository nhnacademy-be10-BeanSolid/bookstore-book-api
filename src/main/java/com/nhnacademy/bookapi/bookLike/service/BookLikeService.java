package com.nhnacademy.bookapi.bookLike.service;

import com.nhnacademy.bookapi.bookLike.controller.request.BookLikeCreateRequest;
import com.nhnacademy.bookapi.bookLike.controller.response.BookLikeResponse;

import java.util.List;

public interface BookLikeService {

    BookLikeResponse createBookLike(BookLikeCreateRequest request);

    List<BookLikeResponse> getBookLikesByUserId(String userId);

    List<BookLikeResponse> getBookLikesByBookId(Long bookId);

    void deleteBookLikeByUserIdAndBookId(String userId, Long bookId);

    void deleteBookLikeByBookId(Long bookId);
}
