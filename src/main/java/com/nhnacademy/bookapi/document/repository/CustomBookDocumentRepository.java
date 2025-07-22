package com.nhnacademy.bookapi.document.repository;

import com.nhnacademy.bookapi.book.domain.response.SimpleBookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomBookDocumentRepository {
    Page<SimpleBookResponse> searchByKeyword(String keyword, Pageable pageable);

    void increaseViewCount(String id, Long viewCount);

    Page<SimpleBookResponse> findAllSimpleBookResponses(Pageable pageable);

    Page<SimpleBookResponse> findAllSimpleBookResponses(Long categoryId, Pageable pageable);
}
