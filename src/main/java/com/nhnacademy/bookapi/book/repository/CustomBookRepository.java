package com.nhnacademy.bookapi.book.repository;

import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookOrderResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;

import java.util.List;
import java.util.Optional;

public interface CustomBookRepository {

    Optional<BookResponse> findBookResponseById(Long id);

    // 도서 상세 정보
    Optional<BookDetailResponse> findBookDetailResponseByBookId(Long bookId);

    int countBookCategoryByBookId(Long bookId);

    // 주문 api 에서 받아갈 정보
    List<BookOrderResponse> findBookOrderResponsesById(List<Long> ids);
}
