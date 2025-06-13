package com.nhnacademy.bookapi.booktag.domain.response;

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
