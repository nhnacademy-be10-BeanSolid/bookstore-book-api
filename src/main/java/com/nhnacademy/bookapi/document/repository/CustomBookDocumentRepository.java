package com.nhnacademy.bookapi.document.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.bookapi.book.domain.response.SimpleBookResponse;
import com.nhnacademy.bookapi.document.BookDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomBookDocumentRepository {
    Page<SimpleBookResponse> searchByKeyword(String keyword, Pageable pageable);

}
