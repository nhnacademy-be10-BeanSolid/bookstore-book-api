package com.nhnacademy.bookapi.bookLike.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookLikeCreateRequest {

    String userId;

    Long bookId;
}
