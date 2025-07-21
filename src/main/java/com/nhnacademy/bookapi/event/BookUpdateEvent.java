package com.nhnacademy.bookapi.event;

import com.nhnacademy.bookapi.book.domain.Book;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookUpdateEvent {
    private final Book book;
    private final Long reviewCount;
    private final Double rating;
}