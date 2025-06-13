package com.nhnacademy.bookapi.bookcategory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookCategoryController.class)
class BookCategoryControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookCategoryService bookCategoryService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("카테고리 전체 조회")
    void getAllCategories() throws Exception {
        BookCategory parent = new BookCategory("Parent", null);
        parent.setCategoryId(1L);
        parent.setCreatedAt(LocalDateTime.now());

        BookCategory child = new BookCategory("Child", null);
        child.setCategoryId(2L);
        child.setCreatedAt(LocalDateTime.now());

        List<BookCategory> categories = Arrays.asList(parent, child);

        given(bookCategoryService.getAllCategories()).willReturn(categories);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].categoryId").value(1L))
                .andExpect(jsonPath("$[1].categoryId").value(2L));
    }

    @Test
    @DisplayName("카테고리 단건 조회")
    void getCategoryById() throws Exception {
        BookCategory parent = new BookCategory("Parent", null);
        parent.setCategoryId(1L);
        parent.setCreatedAt(LocalDateTime.now());

        given(bookCategoryService.getCategoryById(1L)).willReturn(parent);

        mockMvc.perform(get("/categories/{categoryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("Parent"));
    }

    @Test
    @DisplayName("카테고리 생성")
    void createCategory() throws Exception {
        BookCategoryCreateRequest request = new BookCategoryCreateRequest("NewCategory", null);
        BookCategory created = new BookCategory("NewCategory", null);
        created.setCategoryId(10L);
        created.setCreatedAt(LocalDateTime.now());

        given(bookCategoryService.createCategory(any(BookCategory.class))).willReturn(created);

        mockMvc.perform(post("/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/10"))
                .andExpect(jsonPath("$.categoryId").value(10L))
                .andExpect(jsonPath("$.categoryName").value("NewCategory"));
    }

    @Test
    @DisplayName("카테고리 생성 - 유효성 검사 실패")
    void createCategoryValidFailTest() throws Exception {
        BookCategoryCreateRequest request = new BookCategoryCreateRequest(null, null);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리 수정")
    void updateCategory() throws Exception {
        BookCategoryUpdateRequest request = new BookCategoryUpdateRequest("Updated", null);
        BookCategory updated = new BookCategory("Updated", null);
        updated.setCategoryId(1L);
        updated.setCreatedAt(LocalDateTime.now());
        updated.setUpdatedAt(LocalDateTime.now());

        given(bookCategoryService.updateCategory(eq(1L),
                any(BookCategory.class))).willReturn(updated);

        mockMvc.perform(patch("/categories/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("Updated"));
    }

    @Test
    @DisplayName("카테고리 수정 - 유효성 검사 실패")
    void updateCategoryValidFailTest() throws Exception {
        BookCategoryUpdateRequest request = new BookCategoryUpdateRequest(null, null);

        mockMvc.perform(patch("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리 삭제")
    void deleteCategory() throws Exception {
        willDoNothing().given(bookCategoryService).deleteCategory(1L);

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("부모 카테고리가 있는 카테고리 생성")
    void createCategory_withParent() throws Exception {
        // 부모 카테고리 준비
        BookCategory parent = new BookCategory("ParentCategory", null);
        parent.setCategoryId(1L);
        parent.setCreatedAt(LocalDateTime.now());

        // 요청 객체: 부모 ID 포함
        BookCategoryCreateRequest request = new BookCategoryCreateRequest("ChildCategory", 1L);

        // 자식 카테고리 생성 결과
        BookCategory child = new BookCategory("ChildCategory", parent);
        child.setCategoryId(2L);
        child.setCreatedAt(LocalDateTime.now());

        // 부모 카테고리 존재 여부 및 조회 목 세팅
        given(bookCategoryService.existsCategory(1L)).willReturn(true);
        given(bookCategoryService.getCategoryById(1L)).willReturn(parent);
        given(bookCategoryService.createCategory(any(BookCategory.class))).willReturn(child);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/2"))
                .andExpect(jsonPath("$.categoryId").value(2L))
                .andExpect(jsonPath("$.parentId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("ChildCategory"));
    }
}