package com.nhnacademy.bookapi.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.request.BookStockReduceRequest;
import com.nhnacademy.bookapi.book.domain.response.*;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.service.BookSearchService;
import com.nhnacademy.bookapi.book.service.BookService;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import org.hamcrest.Matchers;
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
import java.util.HashSet;
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
    @MockBean
    BookSearchService searchService;

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
    @DisplayName("외부검색")
    void searchBook() throws Exception {
        BookSearchResponse mockResponse = new BookSearchResponse();
        mockResponse.setItems(List.of());

        when(searchService.searchBook("자바", 1))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/books-search")
                        .param("query", "자바")
                        .param("start", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @DisplayName("도서 전체 조회")
    void getAllBooks() throws Exception{
        Pageable pageable = PageRequest.of(0, 4);
        List<SimpleBookResponse> content = List.of(
                new SimpleBookResponse(1L, "테스트1", "작가", 10000, 20, null, 1L),
                new SimpleBookResponse(2L, "테스트2", "작가", 2000, 30, null, 3L)
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
    }

    @Test
    @DisplayName("도서 생성")
    void createBook() throws Exception{
        Set<Long> categoryIds = Set.of(1L);

        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test000000000", 10000, 5000, false, 100, null, categoryIds);

        Book book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        ReflectionTestUtils.setField(book, "title", "타이틀");
        ReflectionTestUtils.setField(book, "author", "작가");
        ReflectionTestUtils.setField(book, "isbn", "test000000000");
        ReflectionTestUtils.setField(book, "bookCategories", Set.of(category));
        ReflectionTestUtils.setField(book, "status", BookStatus.ON_SALE);

        given(bookService.createBook(any(BookCreateRequest.class))).willReturn(BookResponse.from(book));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", "tester")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/books/1"))
                .andExpect(jsonPath("$.title").value("타이틀"))
                .andExpect(jsonPath("$.isbn").value("test000000000"))
                .andExpect(jsonPath("$.author").value("작가"))
                .andExpect(jsonPath("$.bookCategories").value(Matchers.hasItem("카테고리")));
    }

    @Test
    @DisplayName("도서 생성 - 유효성 검사 실패")
    void createBook_validationFail() throws Exception {
        // 재고량 null 요청
        BookCreateRequest badRequest = new BookCreateRequest(
                "타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789", 10000, 5000, false, null, null, new HashSet<>());

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", "tester")
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("도서 수정")
    void updateBook() throws Exception{
        // 포장 여부 > false, 도서 상태 > SALE_END
        BookUpdateRequest request = new BookUpdateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.of(2020,10,19), 10000, 5000, false, BookStatus.SALE_END.toString(), 100);

        Book book1 = new Book();
        ReflectionTestUtils.setField(book1,"id",1L);
        ReflectionTestUtils.setField(book1,"wrappable", true);
        ReflectionTestUtils.setField(book1, "status", BookStatus.ON_SALE);

        book1.updateFrom(request);
        BookDetailResponse response = BookDetailResponse.from(book1, 2);

        given(bookService.updateBook(1L, request)).willReturn(response);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.wrappable").value(false))
                .andExpect(jsonPath("$.status").value(BookStatus.SALE_END.getLabel()));

        verify(bookService, times(1)).updateBook(eq(1L), any(BookUpdateRequest.class));
    }

    @Test
    @DisplayName("도서 수정 - 유효성 검사 실패")
    void updateBook_validationFail() throws Exception {
        BookUpdateRequest badRequest = new BookUpdateRequest(null, "설명", "목차", "출판사", "작가",
                LocalDate.of(2020,10,19), 10000, 5000, true, BookStatus.SALE_END.toString(), 100);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("도서 삭제")
    void deleteBook_success() throws Exception {
        willDoNothing().given(bookService).deleteBook(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
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
                0
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
                0
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
    @DisplayName("도서 검색 - 엘라스틱 서치")
    void getSimpleBookResponseByBookKeyWord_success() throws Exception {
        String keyword = "작가1";
        Pageable pageable = PageRequest.of(0, 4);
        List<SimpleBookResponse> content = List.of(
                new SimpleBookResponse(1L, "테스트책1", "작가1", 10000, 10, null, 5L),
                new SimpleBookResponse(2L, "테스트책2", "작가1", 8000, 3, null, 8L)
        );
        Page<SimpleBookResponse> mockPage = new PageImpl<>(content, pageable, content.size());

        when(bookService.getSimpleBookResponseByKeyword(eq(keyword), any(Pageable.class)))
                .thenReturn(mockPage);

        mockMvc.perform(get("/search")
                        .param("keyword", keyword)
                        .param("page", "0")
                        .param("size", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("테스트책1"))
                .andExpect(jsonPath("$.content[1].title").value("테스트책2"));

        verify(bookService, times(1)).getSimpleBookResponseByKeyword(eq(keyword), any(Pageable.class));
    }
}
