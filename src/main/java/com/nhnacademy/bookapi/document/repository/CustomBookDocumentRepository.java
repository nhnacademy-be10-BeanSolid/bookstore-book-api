package com.nhnacademy.bookapi.document.repository;

import com.nhnacademy.bookapi.document.BookDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomBookDocumentRepository {
    Page<BookDocument> searchByKeyword(String keyword, Pageable pageable);

}
