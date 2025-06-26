package com.nhnacademy.bookapi.book.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AladinItem(
        String title, // 상품명
        String link, // 개별 상품의 링크
        String author,
        String pubDate,
        String description,// 설명은 에디터로 받아와야한다
        String isbn, // 10자리
        String isbn13, // 13자리
        Integer priceSales, // 판매가
        Integer priceStandard, // 정가
        String cover,
        String publisher,
        Integer categoryId
)
{}