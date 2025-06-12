package com.nhnacademy.bookapi.book.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookTagMapCreateRequest {

    private Long bookId;
    private Long tagId;
}
