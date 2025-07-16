package com.nhnacademy.bookapi.bookcategory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryNodeResponse;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
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
        Pageable pageable = PageRequest.of(0, 10);

        BookCategoryResponse parentResponse = new BookCategoryResponse(1L,
                "Parent", null, null, LocalDateTime.now(), LocalDateTime.now());
        BookCategoryResponse childResponse = new BookCategoryResponse(2L,
                "Child",1L,"Parent", LocalDateTime.now(), LocalDateTime.now());
        Page<BookCategoryResponse> page = new PageImpl<>(List.of(parentResponse, childResponse), pageable, 2);

        given(bookCategoryService.getAllCategories(any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].categoryId").value(1L))
                .andExpect(jsonPath("$.content[0].categoryName").value("Parent"))
                .andExpect(jsonPath("$.content[0].parentCategoryName").isEmpty())
                .andExpect(jsonPath("$.content[1].categoryId").value(2L))
                .andExpect(jsonPath("$.content[1].categoryName").value("Child"))
                .andExpect(jsonPath("$.content[1].parentCategoryName").value("Parent"));
    }

    @Test
    @DisplayName("카테고리 단건 조회")
    void getCategoryById() throws Exception {
        BookCategoryResponse response = new BookCategoryResponse(1L,
                "Parent", null, null, LocalDateTime.now(), LocalDateTime.now());

        given(bookCategoryService.getCategoryById(1L)).willReturn(response);

        mockMvc.perform(get("/categories/{categoryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("Parent"))
                .andExpect(jsonPath("$.parentCategoryName").isEmpty());
    }

    @Test
    @DisplayName("카테고리 생성")
    void createCategory() throws Exception {
        BookCategoryCreateRequest request = new BookCategoryCreateRequest("NewCategory", null);
        BookCategoryResponse response = new BookCategoryResponse(10L,
                "NewCategory", null, null, LocalDateTime.now(), LocalDateTime.now());
        given(bookCategoryService.createCategory(request)).willReturn(response);

        mockMvc.perform(post("/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header("X-USER-ID", "test"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/10"))
                .andExpect(jsonPath("$.categoryId").value(10L))
                .andExpect(jsonPath("$.categoryName").value("NewCategory"));
    }

    @Test
    @DisplayName("카테고리 생성 - 유효성 검사 실패")
    void createCategory_validFail() throws Exception {
        BookCategoryCreateRequest request = new BookCategoryCreateRequest(null, null);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "test"))
                .andExpect(status().isBadRequest());
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

        BookCategoryResponse response = new BookCategoryResponse(2L, "ChildCategory", 1L,"ParentCategory", LocalDateTime.now(), LocalDateTime.now());

        given(bookCategoryService.createCategory(any(BookCategoryCreateRequest.class)))
                .willReturn(response);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "test"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/2"))
                .andExpect(jsonPath("$.categoryId").value(2L))
                .andExpect(jsonPath("$.parentCategoryName").value("ParentCategory"))
                .andExpect(jsonPath("$.categoryName").value("ChildCategory"));
    }

    @Test
    @DisplayName("카테고리 수정")
    void updateCategory() throws Exception {
        BookCategoryUpdateRequest request = new BookCategoryUpdateRequest("Updated", null);
        BookCategoryResponse updated = new BookCategoryResponse(1L, "Updated", null, null, LocalDateTime.now(), LocalDateTime.now());

        given(bookCategoryService.updateCategory(eq(1L),
                any(BookCategoryUpdateRequest.class))).willReturn(updated);

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("Updated"));
    }

    @Test
    @DisplayName("카테고리 수정 - 유효성 검사 실패")
    void updateCategory_validFail() throws Exception {
        BookCategoryUpdateRequest request = new BookCategoryUpdateRequest(null, null);

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-USER-ID", "test"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리 삭제")
    void deleteCategory() throws Exception {
        willDoNothing().given(bookCategoryService).deleteCategory(1L);

        mockMvc.perform(delete("/categories/1")
                        .header("X-USER-ID", "test"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("카테고리 삭제 - 헤더 검증 실패")
    void deleteCategory_headerException() throws Exception {
        willDoNothing().given(bookCategoryService).deleteCategory(1L);

        mockMvc.perform(delete("/categories/1")
                        .header("X-USER-ID", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리 트리 정보")
    void getCategoryTree() throws Exception {
        BookCategoryNodeResponse child1 = new BookCategoryNodeResponse(2L, "추리소설", new ArrayList<>());
        BookCategoryNodeResponse child2 = new BookCategoryNodeResponse(3L, "공포소설", new ArrayList<>());
        BookCategoryNodeResponse root = new BookCategoryNodeResponse(1L, "소설", List.of(child1, child2));

        BookCategoryNodeResponse root1 = new BookCategoryNodeResponse(4L, "만화", new ArrayList<>());

        given(bookCategoryService.getCategoryTree()).willReturn(List.of(root, root1));

        mockMvc.perform(get("/categories/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(1L))
                .andExpect(jsonPath("$[0].children.length()").value(2))
                .andExpect(jsonPath("$[0].children[0].categoryName").value("추리소설"))
                .andExpect(jsonPath("$[0].children[1].categoryName").value("공포소설"))
                .andExpect(jsonPath("$[1].categoryId").value(4L));
    }
}