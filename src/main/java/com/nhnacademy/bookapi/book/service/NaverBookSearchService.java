package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.domain.openapi.BookSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class NaverBookSearchService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver-client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public BookSearchResponse searchBook(String query, int start) {

        log.info("Query: {}", query);

        String url = "https://openapi.naver.com/v1/search/book.json?query=" + query + "&start=" + start;

        log.info("URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-Naver-Client-Id", "TxeWzDGQGvVuWtukpNQ3");
//        headers.set("X-Naver-Client-Secret", "9EZsM4pPL6");
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<BookSearchResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                BookSearchResponse.class
        );

        return response.getBody();
    }
}
