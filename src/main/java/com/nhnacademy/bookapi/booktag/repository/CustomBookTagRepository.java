package com.nhnacademy.bookapi.booktag.repository;

import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;

import java.util.List;
import java.util.Optional;

public interface CustomBookTagRepository {

    Optional<BookTagResponse> findBookTagResponseById(Long id);

    List<BookTagResponse> findAllBookTagResponses();
}
