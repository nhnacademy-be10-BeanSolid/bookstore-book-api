package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.adapter.NaverBookClient;
import com.nhnacademy.bookapi.book.domain.response.BookItemResponse;
import com.nhnacademy.bookapi.book.domain.response.BookSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSearchService {

    private final NaverBookClient naverBookClient;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver-client-secret}")
    private String clientSecret;

    public BookSearchResponse searchBook(String query, int start) {
        BookSearchResponse response = naverBookClient.searchBook(clientId, clientSecret, query, start);

        if (response.getItems() != null) {
            for (BookItemResponse item : response.getItems()) {
                String pubDate = item.getPubdate();
                if (pubDate != null && pubDate.length() == 8) {
                    String formatDate = pubDate.substring(0, 4) + "-" +
                            pubDate.substring(4, 6) + "-" +
                            pubDate.substring(6, 8);
                    item.setPubdate(formatDate);
                }
                String author = item.getAuthor();
                if (author != null && author.contains("^")) {
                    item.setAuthor(author.replace("^", ", "));
                }
            }
        }

        return response;
    }
}
