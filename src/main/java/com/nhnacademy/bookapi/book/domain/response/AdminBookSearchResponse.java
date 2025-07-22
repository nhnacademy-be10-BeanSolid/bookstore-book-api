package com.nhnacademy.bookapi.book.domain.response;

public record AdminBookSearchResponse(
    Long bookId,
    String title,
    String author
) {}
