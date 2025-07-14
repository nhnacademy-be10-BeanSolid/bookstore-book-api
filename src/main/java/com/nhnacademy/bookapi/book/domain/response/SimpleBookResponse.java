package com.nhnacademy.bookapi.book.domain.response;

public record SimpleBookResponse(
        long id,
        String title,
        String author,
        int salePrice,
        int stock,
        String image,
        long viewCount
) {
}