package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.domain.BookSearchResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class BookSearchApiService {

    // 아이디, 시크릿 properties 에 저장

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver-client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public BookSearchResponse searchBook(String query, int start) {

        log.info("Query: {}", query);

        // 외부 api url
        String url = "https://openapi.naver.com/v1/search/book.json?query=" + query + "&start=" + start;

        log.info("URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
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
