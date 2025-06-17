package com.nhnacademy.bookapi.book.repository;

import com.nhnacademy.bookapi.book.domain.response.BookResponse;

import java.util.List;
import java.util.Optional;

public interface CustomBookRepository {

    Optional<BookResponse> findBookResponseById(Long id);

    List<BookResponse> findBookResponsesByAuthor(String author);

    List<BookResponse> findBookDetailByPublisher(String publisher);

//    List<BookTagMapResponse> findBookTagMapResponseByBookId(Long id);
}
