package com.nhnacademy.bookapi.book.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "aladinClient", url = "http://www.aladin.co.kr/ttb/api")
public interface AladinClient {

    @GetMapping(value = "/ItemSearch.aspx")
    // AladinSearchResponse
    String searchItem(
            // 필수
            @RequestParam("ttbkey") String ttbKey,
            @RequestParam("Query") String query, // 검색어

            // 필수 x
            @RequestParam(value = "start", required = false) Integer start,
            @RequestParam(value = "MaxResults", required = false) Integer maxResults,

            @RequestParam(value = "output") String output
    );
}