package com.nhnacademy.bookapi.booktag.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookTagMapResponse {

    Long bookId;

    Long tagId;
}
