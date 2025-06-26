package com.nhnacademy.bookapi.book.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AladinSearchResponse (
        // 최상위 dto
        String title, // API 결과의 제목
        String link, // 결과의 알라딘 링크
        Integer totalResults,
        Integer startIndex,
        Integer itemsPerPage,
        String query, // 검색어
        List<AladinItem> item // 상품정보
)
{}
