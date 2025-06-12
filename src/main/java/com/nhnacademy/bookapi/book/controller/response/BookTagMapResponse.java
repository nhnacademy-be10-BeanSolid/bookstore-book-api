package com.nhnacademy.bookapi.book.controller.response;

import com.nhnacademy.bookapi.book.domain.BookTagMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookTagMapResponse {

    Long bookId;

    Long tagId;

    public static BookTagMapResponse of(BookTagMap bookTagMap) {
        return new BookTagMapResponse(
                bookTagMap.getBook().getId(),
                bookTagMap.getTag().getTagId()
        );
    }
}
