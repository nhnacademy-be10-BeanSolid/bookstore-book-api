package com.nhnacademy.bookapi.book.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.book.feignclient.AladinClient;
import com.nhnacademy.bookapi.book.feignclient.dto.AladinSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSearchService {

    private final AladinClient aladinClient;
    private final ObjectMapper objectMapper;

    @Value("${aladin.ttbkey}")
    private String ttbKey;

    public AladinSearchResponse search(String query, Integer start, Integer maxResults) {
        String raw = aladinClient.searchItem(ttbKey, query, start, maxResults, "js");
        log.info("알라딘 응답 {}", raw);
        if (raw.endsWith("};")) {
            raw = raw.substring(0, raw.length() - 1);
        }
        raw = raw.replace("\\'", "'");
        log.info("파싱 {}", raw);
        try {
            return objectMapper.readValue(raw, AladinSearchResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
