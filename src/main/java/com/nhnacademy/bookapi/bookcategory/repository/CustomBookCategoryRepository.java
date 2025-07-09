package com.nhnacademy.bookapi.bookcategory.repository;

import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomBookCategoryRepository {

    Optional<BookCategoryResponse> findBookCategoryResponseById(Long id);

    Page<BookCategoryResponse> findAllBookCategoryResponse(Pageable pageable);

    BookCategoryMapResponse findBookCategoryMapResponse(Long bookId);
}
