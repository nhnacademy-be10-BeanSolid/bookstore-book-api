package com.nhnacademy.bookapi.booktag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagUpdateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
import com.nhnacademy.bookapi.booktag.service.BookTagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookTagController.class)
class BookTagControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookTagService bookTagService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("전체 조회")
    void getBookTags() throws Exception {
        List<BookTagResponse> tags = List.of(
                new BookTagResponse(1L, "tag1"),
                new BookTagResponse(2L, "tag2")
        );

        given(bookTagService.getBookTags()).willReturn(tags);

        mockMvc.perform(get("/book-tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].tagId").value(1L))
                .andExpect(jsonPath("$[0].name").value("tag1"));
    }

    @Test
    @DisplayName("단일 조회")
    void getBookTag() throws Exception {
        BookTagResponse response = new BookTagResponse(1L, "tag1");
        given(bookTagService.getBookTag(1L)).willReturn(response);

        mockMvc.perform(get("/book-tags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId").value(1L))
                .andExpect(jsonPath("$.name").value("tag1"));
    }

    @Test
    @DisplayName("태그 생성")
    void createBookTag() throws Exception {
        BookTagCreateRequest request = new BookTagCreateRequest("tag1");
        BookTagResponse response = new BookTagResponse(1L, "tag1");
        given(bookTagService.createBookTag(request)).willReturn(response);

        mockMvc.perform(post("/book-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/book-tags/1"))
                .andExpect(jsonPath("$.tagId").value(1L))
                .andExpect(jsonPath("$.name").value("tag1"));
    }

    @Test
    @DisplayName("태그 생성 - 유효성 검사 실패")
    void createBookTagValidFailTest() throws Exception {
        BookTagCreateRequest request = new BookTagCreateRequest(null);

        mockMvc.perform(post("/book-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("태그 수정")
    void updateBookTag() throws Exception {
        BookTagUpdateRequest request = new BookTagUpdateRequest("tag2");
        BookTagResponse updateResponse = new BookTagResponse(1L, "tag2");

        given(bookTagService.updateBookTag(eq(1L), any(BookTagUpdateRequest.class)))
                .willReturn(updateResponse);

        mockMvc.perform(patch("/book-tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId").value(1L))
                .andExpect(jsonPath("$.name").value("tag2"));
    }

    @Test
    @DisplayName("태그 수정 - 유효성 검사 실패")
    void updateBookTagValidFailTest() throws Exception {
        BookTagUpdateRequest request = new BookTagUpdateRequest(null);

        mockMvc.perform(patch("/book-tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("태그 삭제")
    void deleteBookTag() throws Exception {
        doNothing().when(bookTagService).deleteBookTag(1L);

        mockMvc.perform(delete("/book-tags/1"))
                .andExpect(status().isNoContent());
    }

}