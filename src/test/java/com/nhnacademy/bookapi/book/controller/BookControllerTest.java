package com.nhnacademy.bookapi.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.request.BookStockReduceRequest;
import com.nhnacademy.bookapi.book.domain.response.*;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.service.BookService;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    BookService bookService;

    BookTag tag;
    BookCategory category;

    @BeforeEach
    void setUp() {
        category = new BookCategory("카테고리", null);
        ReflectionTestUtils.setField(category, "categoryId", 1L);

        tag = new BookTag("태그");
        ReflectionTestUtils.setField(tag, "tagId", 1L);
    }

    @Test
    @DisplayName("도서 전체 조회")
    void getAllBooks() throws Exception{
        Pageable pageable = PageRequest.of(0, 4);
        List<SimpleBookResponse> content = List.of(
                new SimpleBookResponse(1L, "테스트1", "작가", 10000, 20, null, 1L, 0L, 0.0),
                new SimpleBookResponse(2L, "테스트2", "작가", 2000, 30, null, 3L, 0L, 0.0)
        );
        Page<SimpleBookResponse> pageResult = new PageImpl<>(content, pageable, content.size());

        given(bookService.getAllBooks(pageable)).willReturn(pageResult);

        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L));
    }

    @Test
    @DisplayName("카테고리를 가지고 있는 도서 리스트")
    void getAllBooksByCategory() throws Exception {
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 4);

        List<SimpleBookResponse> content = List.of(
                new SimpleBookResponse(1L, "테스트1", "작가", 10000, 20, null, 1L, 0L, 0.0),
                new SimpleBookResponse(2L, "테스트2", "작가", 2000, 30, null, 3L, 0L, 0.0)
        );
        Page<SimpleBookResponse> pageResult = new PageImpl<>(content, pageable, content.size());

        given(bookService.getAllBooks(categoryId, pageable)).willReturn(pageResult);

        mockMvc.perform(get("/books/categories/1")
                        .param("page", "0")
                        .param("size", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L));
    }

    @Test
    @DisplayName("도서 상세정보 조회")
    void getBookDetailById() throws Exception{
        BookDetailResponse mockResponse = new BookDetailResponse(
                1L,
                "테스트책",
                "설명",
                "목차",
                "출판사",
                "작가",
                LocalDate.of(2020, 12, 2),
                "1234567891011",
                10000,
                9000,
                true,
                LocalDateTime.of(2020, 12,3, 12, 10),
                null,
                BookStatus.ON_SALE.getLabel(),
                100,
                null,
                List.of(new BookCategoryResponse(1L, "카테고리", null, null,
                        LocalDateTime.of(2000, 1, 1, 1, 1, 1), null)),
                null,
                3
        );

        given(bookService.getBookDetailResponseByBookId(1L)).willReturn(mockResponse);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("테스트책"))
                .andExpect(jsonPath("$.isbn").value("1234567891011"))
                .andExpect(jsonPath("$.likeCount").value(3));

        verify(bookService, times(1)).increaseViewCount(1L);
    }

    @Test
    @DisplayName("주문상품 정보 전달")
    void getBookOrderResponse_success() throws Exception {
        Book book = new Book(
                1L,
                "테스트1",
                "설명",
                "목차",
                "작가",
                "출판사",
                LocalDate.now(),
                "test000000001",
                10000,
                5000,
                false,
                null,
                null,
                BookStatus.ON_SALE,
                100,
                null,
                Set.of(tag),
                Set.of(category),
                0L
        );
        Book book1 = new Book(
                2L,
                "테스트2",
                "설명",
                "목차",
                "작가",
                "출판사",
                LocalDate.now(),
                "test000000001",
                10000,
                5000,
                false,
                null,
                null,
                BookStatus.ON_SALE,
                100,
                null,
                Set.of(tag),
                Set.of(category),
                0L
        );

        List<BookOrderResponse> response = List.of(
                BookOrderResponse.from(book),
                BookOrderResponse.from(book1)
        );

        given(bookService.getBookOrderResponseByBookIds(List.of(1L, 2L))).willReturn(response);

        mockMvc.perform(get("/books/ids")
                        .param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("재고 최신화")
    void getBookOrderResponse() throws Exception {
        List<BookStockReduceRequest> requests = List.of(new BookStockReduceRequest(1L, 10));

        willDoNothing().given(bookService).updateBookStock(requests);

        mockMvc.perform(put("/book-reduce")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk());

        verify(bookService, times(1)).updateBookStock(requests);
    }

    @Test
    @DisplayName("재고 최신화 - 유효성 검사 실패")
    void getBookOrderResponse_vaildationFail() throws Exception {
        List<BookStockReduceRequest> requests = List.of(new BookStockReduceRequest(null, 10));

        willDoNothing().given(bookService).updateBookStock(requests);

        mockMvc.perform(put("/book-reduce")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("도서 검색 - 엘라스틱 서치")
    void getSimpleBookResponseByBookKeyWord_success() throws Exception {
        String keyword = "작가1";
        Pageable pageable = PageRequest.of(0, 4);
        List<SimpleBookResponse> content = List.of(
                new SimpleBookResponse(1L, "테스트책1", "작가1", 10000, 10, null, 5L, 0L, 0.0),
                new SimpleBookResponse(2L, "테스트책2", "작가1", 8000, 3, null, 8L, 0L, 0.0)
        );
        Page<SimpleBookResponse> mockPage = new PageImpl<>(content, pageable, content.size());

        when(bookService.getSimpleBookResponseByKeyword(eq(keyword), any(Pageable.class)))
                .thenReturn(mockPage);

        mockMvc.perform(get("/books/search")
                        .param("keyword", keyword)
                        .param("page", "0")
                        .param("size", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("테스트책1"))
                .andExpect(jsonPath("$.content[1].title").value("테스트책2"));

        verify(bookService, times(1)).getSimpleBookResponseByKeyword(eq(keyword), any(Pageable.class));
    }

    @Test
    @DisplayName("유저 서비스에서 인덱스 최신화")
    void updateBookDocumentResponse_success() throws Exception {
        Long reviewCount = 5L;
        Double reviewAverage = 4.3;

        mockMvc.perform(post("/books/1/document")
                        .param("reviewCount", reviewCount.toString())
                        .param("reviewAverage", reviewAverage.toString()))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).updateBookDocument(1L, reviewCount, reviewAverage);
    }

    @Test
    @DisplayName("유저 서비스에서 도서 제목 받기")
    void getTitleByBookId_success() throws Exception {
        String title = "제목";

        when(bookService.getTitleByBookId(anyLong())).thenReturn(title);

        mockMvc.perform(get("/books/1/title"))
                .andExpect(status().isOk())
                .andExpect(content().string(title));

        verify(bookService, times(1)).getTitleByBookId(anyLong());
    }
}