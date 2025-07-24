package com.nhnacademy.bookapi.bookcategory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.adpater.service.UserService;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryMapCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryMapService;
import com.nhnacademy.bookapi.common.exception.ForbiddenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCategoryMapController.class)
class BookCategoryMapControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookCategoryMapService bookCategoryMapService;
    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    Long bookId = 1L;
    Long categoryId = 1L;

    @Test
    @DisplayName("도서에 카테고리 추가")
    void createBookCategoryMap() throws Exception {
        BookCategoryMapCreateRequest request = new BookCategoryMapCreateRequest(categoryId);
        BookCategoryResponse response = new BookCategoryResponse(categoryId, "테스트",
                null, null, LocalDateTime.now(), null);
        BookCategoryMapResponse mapResponse = new BookCategoryMapResponse(bookId, List.of(response));

        willDoNothing().given(userService).getUserAuthorize("admin");
        given(bookCategoryMapService.createBookCategoryMap(bookId, request)).willReturn(mapResponse);

        mockMvc.perform(post("/books/{bookId}/categories", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(bookId))
                .andExpect(jsonPath("$.categories[0].categoryId").value(1));
    }

    @Test
    @DisplayName("도서에 카테고리 추가 - 유효성 검사 실패")
    void createBookCategoryMap_validationFail() throws Exception {
        BookCategoryMapCreateRequest request = new BookCategoryMapCreateRequest(null);

        willDoNothing().given(userService).getUserAuthorize("admin");

        mockMvc.perform(post("/books/1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "admin"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("도서에 카테고리 추가 - 권한 부족")
    void createBookCategoryMap_headerException() throws Exception {
        BookCategoryMapCreateRequest request = new BookCategoryMapCreateRequest(categoryId);

        willThrow(new ForbiddenException("관리자 권한이 필요합니다."))
                .given(userService).getUserAuthorize("test");

        mockMvc.perform(post("/books/1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "test"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("도서에서 카테고리 삭제")
    void deleteBookCategoryMap() throws Exception {
        willDoNothing().given(userService).getUserAuthorize("admin");
        willDoNothing().given(bookCategoryMapService).deleteCategoryMap(bookId, categoryId);

        mockMvc.perform(delete("/books/{bookId}/categories/{categoryId}", bookId, categoryId)
                        .header("X-USER-ID", "admin"))
                .andExpect(status().isNoContent());

        verify(bookCategoryMapService).deleteCategoryMap(bookId, categoryId);
    }

    @Test
    @DisplayName("도서에서 카테고리 삭제 - 권한 부족")
    void deleteBookCategoryMap_headerException() throws Exception {
        willThrow(new ForbiddenException("관리자 권한이 필요합니다."))
                .given(userService).getUserAuthorize("test");
        willDoNothing().given(bookCategoryMapService).deleteCategoryMap(bookId, categoryId);

        mockMvc.perform(delete("/books/{bookId}/categories/{categoryId}", bookId, categoryId)
                        .header("X-USER-ID", "test"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("도서의 카테고리 조회")
    void getBookTagMapResponse() throws Exception {
        BookCategoryResponse categoryResponse = new BookCategoryResponse(categoryId, "카테고리",
                null, null, LocalDateTime.now(), null);
        BookCategoryMapResponse response = new BookCategoryMapResponse(bookId, List.of(categoryResponse));


        given(bookCategoryMapService.getBookCategoryMapResponse(1L)).willReturn(response);

        mockMvc.perform(get("/books/1/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.categories[0].categoryId").value(1))
                .andExpect(jsonPath("$.categories[0].categoryName").value("카테고리"));
    }
}
