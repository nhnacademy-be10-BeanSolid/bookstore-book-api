package com.nhnacademy.bookapi.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.request.BookStockReduceRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookOrderResponse;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.feignclient.BookSearchApiService;
import com.nhnacademy.bookapi.book.service.BookService;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.booklike.domain.BookLike;
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
    BookSearchApiService bookSearchApiService;

    Book book;
    BookTag tag;
    BookCategory category;

    @BeforeEach
    void setUp() {
        category = new BookCategory("소설", null);
        ReflectionTestUtils.setField(category,"categoryId", 1L);
        Set<BookCategory> categories = new HashSet<>();
        categories.add(category);

        tag = new BookTag("Test");
        ReflectionTestUtils.setField(tag,"tagId", 1L);
        Set<BookTag> tags = new HashSet<>();
        tags.add(tag);

        Set<BookLike> bookLikes = new HashSet<>();
        bookLikes.add(new BookLike("user", book));

        book = new Book(
                1L,                    // id, 보통 생성 시엔 null
                "타이틀",
                "설명",
                "목차",
                "작가",
                "출판사",
                LocalDate.now(),
                "test000000000",
                10000,
                5000,
                false,
                LocalDateTime.now(),     // createAt
                LocalDateTime.now(),     // updateAt
                BookStatus.ON_SALE,      // status (필요에 따라 바꿔도 됨)
                100,
                null,                   // image (필요하면 넣기),
                tags,
                categories,
                bookLikes
        );
    }

    @Test
    @DisplayName("도서 전체 조회")
    void getAllBookResponses() throws Exception{
        Book book1 = new Book(
                2L,
                "타이틀",
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
                new HashSet<>()
        );

        BookResponse response1 = BookResponse.from(book);
        BookResponse response2 = BookResponse.from(book1);

        Pageable page = PageRequest.of(0, 10);
        Page<BookResponse> pageResult = new PageImpl<>(List.of(response1, response2), page, 2);

        given(bookService.getAllBooks(page)).willReturn(pageResult);

        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].isbn").value("test000000000"))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].isbn").value("test000000001"));
    }

    @Test
    @DisplayName("도서 상세정보 조회")
    void getBookDetailById() throws Exception{
        Long bookId = book.getId(); // 1L
        BookDetailResponse response = BookDetailResponse.from(book);

        given(bookService.getBookDetailResponseByBookId(bookId)).willReturn(response);

        mockMvc.perform(get("/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("타이틀"))
                .andExpect(jsonPath("$.isbn").value("test000000000"))
                .andExpect(jsonPath("$.likedUsers").value(Matchers.hasItems("user")));
    }

    @Test
    @DisplayName("도서 생성")
    void createBook() throws Exception{
        Set<Long> categoryIds = Set.of(1L);

        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test000000000", 10000, 5000, false, 100, null, categoryIds);

        given(bookService.createBook(any(BookCreateRequest.class))).willReturn(BookResponse.from(book));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/books/1"))
                .andExpect(jsonPath("$.title").value("타이틀"))
                .andExpect(jsonPath("$.isbn").value("test000000000"))
                .andExpect(jsonPath("$.author").value("작가"))
                .andExpect(jsonPath("$.bookCategories").value(Matchers.hasItem("소설")));
    }

    @Test
    @DisplayName("도서 생성 - 유효성 검사 실패")
    void createBook_validFail() throws Exception {
        // 재고량 null 요청
        BookCreateRequest badRequest = new BookCreateRequest(
                "타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789", 10000, 5000, false, null, null, new HashSet<>());

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("도서 수정")
    void updateBook() throws Exception{
        // 포장 여부 > false, 도서 상태 > SALE_END
        BookUpdateRequest request = new BookUpdateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.of(2020,10,19), 10000, 5000, true, BookStatus.SALE_END.toString(), 100);
        book.updateFrom(request);
        BookDetailResponse response = BookDetailResponse.from(book);

        given(bookService.updateBook(1L, request)).willReturn(response);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.wrappable").value(true))
                .andExpect(jsonPath("$.status").value(BookStatus.SALE_END.toString()));
    }

    @Test
    @DisplayName("도서 수정 - 유효성 검사 실패")
    void updateBook_validFail() throws Exception {
        BookUpdateRequest badRequest = new BookUpdateRequest(null, "설명", "목차", "출판사", "작가",
                LocalDate.of(2020,10,19), 10000, 5000, true, BookStatus.SALE_END.toString(), 100);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("도서 삭제")
    void deleteBookSuccessTest() throws Exception {
        willDoNothing().given(bookService).deleteBook(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("주문상품 정보 전달")
    void getBookOrderResponse_success() throws Exception {
        Book book1 = new Book(
                2L,
                "타이틀",
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
                new HashSet<>()
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

        mockMvc.perform(patch("/book-reduce")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk());

        verify(bookService).updateBookStock(requests);
    }
}
