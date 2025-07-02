package com.nhnacademy.bookapi.booklike.repository;

import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomBookLikeRepository {

    Optional<BookLikeResponse> findBookLikeResponseById(Long id);

    Page<BookLikeResponse> findBookLikeResponsesByBookId(Long bookId, Pageable pageable);

    Page<BookLikeResponse> findBookLikeResponsesByUserId(String userId, Pageable pageable);
}
