package com.nhnacademy.bookapi.adpater.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AladinItem(
        String title, // 상품명
        String link, // 개별 상품의 링크
        String author,
        String pubDate,
        String description,
        String isbn, // 10자리
        String isbn13, // 13자리
        Integer priceSales, // 판매가
        Integer priceStandard, // 정가
        String cover,
        String publisher
) {
    public static AladinItem from(AladinItem item, String isbn13) {
        return new AladinItem(
                item.title(),
                item.link(),
                item.author(),
                item.pubDate(),
                item.description(),
                item.isbn(),
                isbn13,
                item.priceSales(),
                item.priceStandard(),
                item.cover(),
                item.publisher()
        );
    }
}