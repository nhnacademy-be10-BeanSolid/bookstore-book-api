package com.nhnacademy.bookapi.book.domain.response;

import lombok.Data;

// 개별 검색 결과

@Data
public class BookItem {

    private String title;

    private String link;

    private String image;

    private String author;

    private String publisher;

    private String pubdate;

    private String isbn;

    private String description;
}
