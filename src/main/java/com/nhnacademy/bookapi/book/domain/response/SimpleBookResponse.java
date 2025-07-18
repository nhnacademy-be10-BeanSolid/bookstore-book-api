package com.nhnacademy.bookapi.book.domain.response;

public record SimpleBookResponse(
        Long id,
        String title,
        String author,
        Integer salePrice,
        Integer stock,
        String image,
        Long viewCount,
        Long ReviewCount,
        Double rating
) {}