package com.nhnacademy.bookapi.booklike.service;

import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface BookLikeService {

    BookLikeResponse createBookLike(Long bookId, String userId);

    Page<BookLikeResponse> getBookLikesByUserId(String userId, Pageable pageable);

    Page<BookLikeResponse> getBookLikesByBookId(Long bookId, Pageable pageable);

    void deleteBookLikeByUserIdAndBookId(String userId, Long bookId);
}
