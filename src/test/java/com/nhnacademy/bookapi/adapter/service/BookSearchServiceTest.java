package com.nhnacademy.bookapi.adapter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.adpater.AladinAdapter;
import com.nhnacademy.bookapi.adpater.domain.AladinItem;
import com.nhnacademy.bookapi.adpater.domain.AladinSearchRequest;
import com.nhnacademy.bookapi.adpater.domain.AladinSearchResponse;
import com.nhnacademy.bookapi.adpater.service.AladinService;
import com.nhnacademy.bookapi.common.service.IsbnConverter;
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
    private AladinAdapter aladinClient;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private IsbnConverter isbnConverter;

    @InjectMocks
    private AladinService bookSearchService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(bookSearchService, "ttbKey", "dummy-ttb");
    }

    @Test
    @DisplayName("알라딘 검색 테스트 - isbn13 없음, 변환 수행")
    void searchBook_withIsbn13Conversion() throws JsonProcessingException {
        // given
        AladinItem item = new AladinItem(
                "제목",
                "링크",
                "작가",
                "출판일",
                "설명",
                "1234567890",
                "",
                27000,
                30000, "이미지",
                "출판사",
                51320
        );

        AladinSearchResponse original = new AladinSearchResponse(
                "제목", 100, 1, 10, "소설", List.of(item)
        );

        String json = new ObjectMapper().writeValueAsString(original);

        when(aladinClient.searchItem("dummy-ttb", "자바", 1, 100, "js"))
                .thenReturn(json);
        when(objectMapper.readValue(anyString(), eq(AladinSearchResponse.class)))
                .thenReturn(original);
        when(isbnConverter.convertIsbn10ToIsbn13("1234567890"))
                .thenReturn("9781234567890");

        AladinSearchResponse result = bookSearchService.search(new AladinSearchRequest("자바", 1, 100));

        assertThat(result.item().getFirst().isbn13()).isEqualTo("9781234567890");
    }
}

