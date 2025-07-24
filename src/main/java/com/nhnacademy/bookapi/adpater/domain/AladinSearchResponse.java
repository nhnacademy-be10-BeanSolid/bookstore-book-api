package com.nhnacademy.bookapi.adpater.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AladinSearchResponse (
        // 최상위 dto
        String title, // API 결과의 제목
        Integer totalResults,
        Integer startIndex,
        Integer itemsPerPage,
        String query, // 검색어
        List<AladinItem> item // 상품정보
) {
    public static AladinSearchResponse from(AladinSearchResponse response,
                                            List<AladinItem> item) {
        return new AladinSearchResponse(
                response.title(),
                response.totalResults(),
                response.startIndex(),
                response.itemsPerPage(),
                response.query(),
                item
        );
    }
}