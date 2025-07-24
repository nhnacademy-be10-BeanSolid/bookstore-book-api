package com.nhnacademy.bookapi.adpater.domain;

import lombok.Getter;

@Getter
public class AladinSearchRequest {
    String query;
    Integer start;
    Integer maxResults;

    public AladinSearchRequest(String query, Integer start, Integer maxResults) {
        this.query = query;
        this.start = start;
        this.maxResults = maxResults;
    }

}
