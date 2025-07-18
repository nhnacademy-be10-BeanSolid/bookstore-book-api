package com.nhnacademy.bookapi;

import com.nhnacademy.bookapi.book.domain.Book;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookCreatedEvent {
    private final Book book;
}