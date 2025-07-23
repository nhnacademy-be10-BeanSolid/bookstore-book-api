package com.nhnacademy.bookapi.booktag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagUpdateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
import com.nhnacademy.bookapi.booktag.service.BookTagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
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
        Pageable pageable = PageRequest.of(0, 10);

        List<BookTagResponse> tags = List.of(
                new BookTagResponse(1L, "tag1"),
                new BookTagResponse(2L, "tag2")
        );
        Page<BookTagResponse> page = new PageImpl<>(tags, pageable, tags.size());

        given(bookTagService.getBookTags(any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/book-tags")
                        .header("X-USER-ID", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].tagId").value(1L))
                .andExpect(jsonPath("$.content[0].tagName").value("tag1"))
                .andExpect(jsonPath("$.content[1].tagId").value(2L))
                .andExpect(jsonPath("$.content[1].tagName").value("tag2"));
    }

    @Test
    @DisplayName("전체 조회 - 헤더 검증 실패")
    void getBookTags_headerException() throws Exception {
        mockMvc.perform(get("/book-tags")
                        .header("X-USER-ID", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일 조회")
    void getBookTag() throws Exception {
        BookTagResponse response = new BookTagResponse(1L, "tag1");

        given(bookTagService.getBookTag(1L)).willReturn(response);

        mockMvc.perform(get("/book-tags/1")
                        .header("X-USER-ID", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId").value(1L))
                .andExpect(jsonPath("$.tagName").value("tag1"));
    }

    @Test
    @DisplayName("태그 생성")
    void createBookTag() throws Exception {
        BookTagCreateRequest request = new BookTagCreateRequest("tag1");
        BookTagResponse response = new BookTagResponse(1L, "tag1");

        given(bookTagService.createBookTag(request)).willReturn(response);

        mockMvc.perform(post("/book-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "test"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/book-tags/1"))
                .andExpect(jsonPath("$.tagId").value(1L))
                .andExpect(jsonPath("$.tagName").value("tag1"));
    }

    @Test
    @DisplayName("태그 생성 - 헤더 검증 실패")
    void createBookTag_headerException() throws Exception {
        BookTagCreateRequest request = new BookTagCreateRequest("tag1");

        mockMvc.perform(post("/book-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "asfghjklqwertyuiopzxcvbnm".repeat(20)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("태그 생성 - 유효성 검사 실패")
    void createBookTag_validationFail() throws Exception {
        BookTagCreateRequest request = new BookTagCreateRequest(null);

        mockMvc.perform(post("/book-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "test"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("태그 수정")
    void updateBookTag() throws Exception {
        BookTagUpdateRequest request = new BookTagUpdateRequest("tag2");
        BookTagResponse updateResponse = new BookTagResponse(1L, "tag2");

        given(bookTagService.updateBookTag(eq(1L), any(BookTagUpdateRequest.class)))
                .willReturn(updateResponse);

        mockMvc.perform(put("/book-tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId").value(1L))
                .andExpect(jsonPath("$.tagName").value("tag2"));
    }

    @Test
    @DisplayName("태그 수정 - 유효성 검사 실패")
    void updateBookTagValidFailTest() throws Exception {
        BookTagUpdateRequest request = new BookTagUpdateRequest("");

        mockMvc.perform(put("/book-tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "test"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("태그 삭제")
    void deleteBookTag() throws Exception {
        willDoNothing().given(bookTagService).deleteBookTag(1L);

        mockMvc.perform(delete("/book-tags/1")
                        .header("X-USER-ID", "test"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("태그 삭제 - 헤더 검증 실패")
    void deleteBookTag_headerException() throws Exception {
        mockMvc.perform(delete("/book-tags/1")
                        .header("X-USER-ID", ""))
                .andExpect(status().isBadRequest());
    }
}