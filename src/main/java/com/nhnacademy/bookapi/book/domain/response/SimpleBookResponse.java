package com.nhnacademy.bookapi.book.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleBookResponse {
    private long id;
    private String title;
    private String author;
    private int salePrice;
    private int stock;
    private String image;
}