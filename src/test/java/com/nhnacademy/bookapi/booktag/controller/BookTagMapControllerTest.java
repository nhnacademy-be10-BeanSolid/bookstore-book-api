package com.nhnacademy.bookapi.booktag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
import com.nhnacademy.bookapi.booktag.service.BookTagMapService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookTagMapController.class)
class BookTagMapControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookTagMapService bookTagMapService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("도서에 태그 추가")
    void createBookTagMap() throws Exception {
        BookTagMapCreateRequest request = new BookTagMapCreateRequest(1L);
        BookTagMapResponse response = new BookTagMapResponse(1L, List.of(new BookTagResponse(1L, "test")));

        given(bookTagMapService.createBookTag(1L, request)).willReturn(response);

        mockMvc.perform(post("/books/1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.tags[0].tagId").value(1))
                .andExpect(jsonPath("$.tags[0].tagName").value("test"));
    }

    @Test
    @DisplayName("도서에 태그 추가 - 유효성 검사 실패")
    void createBookTagMap_validationFail() throws Exception {
        BookTagMapCreateRequest request = new BookTagMapCreateRequest(null);

        mockMvc.perform(post("/books/1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "test"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("도서에 태그 추가 - 헤더 검증 실패")
    void createBookTag_headerException() throws Exception {
        BookTagMapCreateRequest request = new BookTagMapCreateRequest(1L);

        mockMvc.perform(post("/books/1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("도서에서 태그 삭제")
    void deleteBookTagMap() throws Exception {
        doNothing().when(bookTagMapService).deleteBookTag(1L, 1L);

        mockMvc.perform(delete("/books/1/tags/1")
                        .header("X-USER-ID", "test"))
                .andExpect(status().isNoContent());

        verify(bookTagMapService).deleteBookTag(1L, 1L);
    }

    @Test
    @DisplayName("도서에서 태그 삭제 - 헤더 검증 실패")
    void deleteBookTagMap_headerException() throws Exception {
        doNothing().when(bookTagMapService).deleteBookTag(1L, 1L);

        mockMvc.perform(delete("/books/1/tags/1")
                        .header("X-USER-ID", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("도서의 태그 조회")
    void getBookTagMapResponse() throws Exception {
        BookTagMapResponse response = new BookTagMapResponse(1L, List.of(new BookTagResponse(1L, "test")));

        given(bookTagMapService.getBookTagMapResponse(1L)).willReturn(response);

        mockMvc.perform(get("/books/1/tags")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.tags[0].tagId").value(1))
                .andExpect(jsonPath("$.tags[0].tagName").value("test"));
    }
}
