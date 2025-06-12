package com.nhnacademy.bookapi.booktag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.controller.request.BookTagCreateRequest;
import com.nhnacademy.bookapi.booktag.controller.request.BookTagUpdateRequest;
import com.nhnacademy.bookapi.booktag.service.BookTagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookTagController.class)
class BookTagControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BookTagService bookTagService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /book-tags - 전체 조회")
    void getBookTags() throws Exception {
        List<BookTag> tags = List.of(
                new BookTag(1L, "tag1"),
                new BookTag(2L, "tag2")
        );
        given(bookTagService.getBookTags()).willReturn(tags);

        mockMvc.perform(get("/book-tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].tagId").value(1L))
                .andExpect(jsonPath("$[0].name").value("tag1"));
    }

    @Test
    @DisplayName("GET /book-tags/{tagId} - 단일 조회")
    void getBookTag() throws Exception {
        BookTag tag = new BookTag(1L, "tag1");
        given(bookTagService.getBookTag(1L)).willReturn(tag);

        mockMvc.perform(get("/book-tags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId").value(1L))
                .andExpect(jsonPath("$.name").value("tag1"));
    }

    @Test
    @DisplayName("POST /book-tags - 생성")
    void createBookTag() throws Exception {
        BookTagCreateRequest request = new BookTagCreateRequest("tag1");
        BookTag saved = new BookTag(1L, "tag1");
        given(bookTagService.createBookTag(ArgumentMatchers.any(BookTag.class))).willReturn(saved);

        mockMvc.perform(post("/book-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/book-tags/1"))
                .andExpect(jsonPath("$.tagId").value(1L))
                .andExpect(jsonPath("$.name").value("tag1"));
    }

    @Test
    @DisplayName("PATCH /book-tags/{tagId} - 수정")
    void updateBookTag() throws Exception {
        BookTagUpdateRequest request = new BookTagUpdateRequest("tag2");
        BookTag updated = new BookTag(1L, "tag2");
        updated.setTagId(1L);

        given(bookTagService.updateBookTag(ArgumentMatchers.any(BookTag.class))).willReturn(updated);

        mockMvc.perform(patch("/book-tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId").value(1L))
                .andExpect(jsonPath("$.name").value("tag2"));
    }

    @Test
    @DisplayName("DELETE /book-tags/{tagId} - 삭제")
    void deleteBookTag() throws Exception {
        doNothing().when(bookTagService).deleteBookTag(1L);

        mockMvc.perform(delete("/book-tags/1"))
                .andExpect(status().isNoContent());
    }

}