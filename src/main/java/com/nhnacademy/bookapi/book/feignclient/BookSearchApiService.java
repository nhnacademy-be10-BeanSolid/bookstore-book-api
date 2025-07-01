package com.nhnacademy.bookapi.book.feignclient;

import com.nhnacademy.bookapi.book.domain.response.BookItemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSearchApiService {

    private final NaverBookClient naverBookClient;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver-client-secret}")
    private String clientSecret;

    public List<BookItemResponse> searchBook(String query, int start) {
        List<BookItemResponse> items = naverBookClient.searchBook(clientId, clientSecret, query, start).getItems();

        for (BookItemResponse item : items) {
            String pubDate = item.getPubdate();
            if (pubDate != null && pubDate.length() == 8) {
                String formatDate = pubDate.substring(0, 4) + "-" +
                        pubDate.substring(4, 6) + "-" +
                        pubDate.substring(6, 8);
                item.setPubdate(formatDate);
            }
        }
        return items;
    }
}
