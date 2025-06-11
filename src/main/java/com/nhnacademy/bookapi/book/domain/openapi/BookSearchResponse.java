package com.nhnacademy.bookapi.book.domain.openapi;

import lombok.Data;

import java.util.List;

@Data
public class BookSearchResponse {

    // 검색 결과를 생성한 시간
    private String lastBuildDate;

    private int total;

    // 시작 위치
    private int start;

    private int display;

    // 책 정보를 담는 리스트
    private List<BookItem> items;
}
