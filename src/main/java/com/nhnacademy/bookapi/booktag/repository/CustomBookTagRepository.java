package com.nhnacademy.bookapi.booktag.repository;

import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomBookTagRepository {

    Optional<BookTagResponse> findBookTagResponseById(Long id);

    Page<BookTagResponse> findAllBookTagResponses(Pageable pageable);
}
