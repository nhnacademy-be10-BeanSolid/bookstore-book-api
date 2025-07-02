package com.nhnacademy.bookapi.book.feignclient;

import com.nhnacademy.bookapi.book.domain.response.BookSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naverBookClient", url = "https://openapi.naver.com/v1/search")
public interface NaverBookClient {

    @GetMapping("/book.json")
    BookSearchResponse searchBook(
            @RequestHeader("X-Naver-Client-Id") String clientId,
            @RequestHeader("X-Naver-Client-Secret") String clientSecret,
            @RequestParam("query") String query,
            @RequestParam("start") int start
    );
}
