package com.nhnacademy.bookapi.bookcategory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryMapCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryMapService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCategoryMapController.class)
class BookCategoryMapControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookCategoryMapService bookCategoryMapService;

    @Autowired
    ObjectMapper objectMapper;

    Long bookId = 1L;
    Long categoryId = 1L;

    @Test
    @DisplayName("POST /books/{bookId}/categories")
    void createBookCategoryMapTest() throws Exception {
        BookCategoryMapCreateRequest request = new BookCategoryMapCreateRequest(categoryId);
        BookCategoryMapResponse response = new BookCategoryMapResponse(bookId, categoryId);

        given(bookCategoryMapService.createBookCategoryMap(bookId, request)).willReturn(response);

        mockMvc.perform(post("/books/{bookId}/categories", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(bookId))
                .andExpect(jsonPath("$.categoryId").value(categoryId));
    }

    @Test
    @DisplayName("DELETE /books/{bookId}/categories")
    void deleteBookCategoryMapTest() throws Exception {
        doNothing().when(bookCategoryMapService).deleteCategoryMap(bookId, categoryId);

        mockMvc.perform(delete("/books/{bookId}/categories/{categoryId}", bookId, categoryId))
                .andExpect(status().isNoContent());

        verify(bookCategoryMapService).deleteCategoryMap(bookId, categoryId);
    }


}
