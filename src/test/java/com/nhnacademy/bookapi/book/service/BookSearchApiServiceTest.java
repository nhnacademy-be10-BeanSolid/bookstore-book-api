package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.domain.response.BookSearchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookSearchApiServiceTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    BookSearchApiService bookSearchApiService;

    @Test
    void searchBookTest() {
        String query = "포켓몬스터";
        int start = 1;
        String url = "https://openapi.naver.com/v1/search/book.json?query=" + query + "&start=" + start;

        BookSearchResponse bookSearchResponse = new BookSearchResponse();
        bookSearchResponse.setDisplay(1);

        ResponseEntity<BookSearchResponse> response = new ResponseEntity<>(bookSearchResponse, HttpStatus.OK);

        when(restTemplate.exchange(eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(BookSearchResponse.class))).thenReturn(response);

        BookSearchResponse searchBookResponse = bookSearchApiService.searchBook(query, start);

        assertThat(searchBookResponse).isNotNull();
    }
}
