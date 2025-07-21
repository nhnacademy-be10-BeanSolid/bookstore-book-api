package com.nhnacademy.bookapi.event;

import com.nhnacademy.bookapi.book.domain.Book;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookDeleteEvent {
    private final Book book;
}
