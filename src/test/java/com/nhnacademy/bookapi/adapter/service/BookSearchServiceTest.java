package com.nhnacademy.bookapi.adapter.service;

import com.nhnacademy.bookapi.adpater.NaverBookAdapter;
import com.nhnacademy.bookapi.adpater.service.NaverBookService;
import com.nhnacademy.bookapi.book.domain.response.BookItemResponse;
import com.nhnacademy.bookapi.book.domain.response.BookSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookSearchServiceTest {

    @Mock
    private NaverBookAdapter naverBookClient;

    @InjectMocks
    private NaverBookService bookSearchService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(bookSearchService, "clientId", "dummy-client-id");
        ReflectionTestUtils.setField(bookSearchService, "clientSecret", "dummy-client-secret");
    }

    @Test
    @DisplayName("네이버 검색 테스트")
    void searchBook() {
        BookItemResponse item1 = new BookItemResponse();
        item1.setPubdate("20250711");
        item1.setAuthor("김^이");

        BookSearchResponse mockResponse = new BookSearchResponse();
        mockResponse.setItems(List.of(item1));

        when(naverBookClient.searchBook("dummy-client-id", "dummy-client-secret", "자바", 1))
                .thenReturn(mockResponse);

        BookSearchResponse result = bookSearchService.searchBook("자바", 1);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);

        BookItemResponse formattedItem = result.getItems().getFirst();
        assertThat(formattedItem.getPubdate()).isEqualTo("2025-07-11");
        assertThat(formattedItem.getAuthor()).isEqualTo("김, 이");

        verify(naverBookClient, times(1))
                .searchBook("dummy-client-id", "dummy-client-secret", "자바", 1);
    }
}

