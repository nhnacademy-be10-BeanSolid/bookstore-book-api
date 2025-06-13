package com.nhnacademy.bookapi.booklike.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookLikeCreateRequest {
    String userId;
}
