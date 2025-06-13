package com.nhnacademy.bookapi.booktag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.nhnacademy.bookapi.booktag.service.BookTagMapService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    Long bookId = 1L;
    Long tagId = 1L;

    @Test
    @DisplayName("POST /books/{bookId}/tags")
    void createBookTagMapTest() throws Exception {
        BookTagMapCreateRequest request = new BookTagMapCreateRequest(tagId);
        BookTagMapResponse response = new BookTagMapResponse(bookId, tagId);

        given(bookTagMapService.createBookTag(bookId, request)).willReturn(response);

        mockMvc.perform(post("/books/{bookId}/tags", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId").value(tagId))
                .andExpect(jsonPath("$.bookId").value(bookId));
    }

    @Test
    @DisplayName("DELETE /books/{bookId}/tags/{tagId}")
    void deleteBookTagMapTest() throws Exception {
        doNothing().when(bookTagMapService).deleteBookTag(bookId, tagId);

        mockMvc.perform(delete("/books/{bookId}/tags/{tagId}", bookId, tagId))
                .andExpect(status().isNoContent());

        verify(bookTagMapService).deleteBookTag(bookId, tagId);
    }

}
