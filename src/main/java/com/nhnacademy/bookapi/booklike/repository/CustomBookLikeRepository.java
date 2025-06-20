package com.nhnacademy.bookapi.booklike.repository;

import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;

import java.util.List;
import java.util.Optional;

public interface CustomBookLikeRepository {

    Optional<BookLikeResponse> findBookLikeResponseById(Long id);

    List<BookLikeResponse> findBookLikeResponsesByBookId(Long bookId);

    List<BookLikeResponse> findBookLikeResponsesByUserId(String userId);
}
