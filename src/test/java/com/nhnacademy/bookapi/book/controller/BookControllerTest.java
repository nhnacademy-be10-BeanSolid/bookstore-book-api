package com.nhnacademy.bookapi.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookSearchResponse;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.service.BookSearchApiService;
import com.nhnacademy.bookapi.book.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    BookService bookService;

    @MockBean
    BookSearchApiService bookSearchService;

    Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .title("타이틀")
                .description("설명")
                .toc("목차")
                .publisher("출판사")
                .author("작가")
                .publishedDate(LocalDate.now())
                .isbn("test000000000")
                .originalPrice(10000)
                .salePrice(5000)
                .wrappable(false)
                .stock(100)
                .build();
        ReflectionTestUtils.setField(book, "id", 1L);
    }

    @Test
    @DisplayName("GET /books-search?query=")
    void bookServiceTest() throws Exception {
        String query = "포켓몬스터";

        given(bookSearchService.searchBook(query, 1)).willReturn(new BookSearchResponse());

        mockMvc.perform(get("/books-search?query=" + query))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /books/{bookId}")
    void getBookTest() throws Exception{
        Long bookId = book.getId();
        BookDetailResponse response = BookDetailResponse.of(book);

        given(bookService.getBookDetailByBookId(bookId)).willReturn(response);

        mockMvc.perform(get("/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(response.getTitle()))
                .andExpect(jsonPath("$.isbn").value(response.getIsbn()))
                .andExpect(jsonPath("$.author").value(response.getAuthor()));
    }

    @Test
    @DisplayName("GET /authors/{author}")
    void getAuthorTest() throws Exception{
        String author = book.getAuthor();
        BookDetailResponse response = BookDetailResponse.of(book);

        given(bookService.getBooksByAuthor(author)).willReturn(List.of(response));

        mockMvc.perform(get("/authors/{author}", author))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].author").value(author));
    }

    @Test
    @DisplayName("GET /publishers/{publisher}")
    void getBooksByPublisherTest() throws Exception{
        String publisher = book.getPublisher();
        BookDetailResponse response = BookDetailResponse.of(book);

        given(bookService.getBooksByPublisher(publisher)).willReturn(List.of(response));

        mockMvc.perform(get("/publishers/{publisher}", publisher))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].publisher").value(publisher));
    }


    @Test
    @DisplayName("POST /books")
    void createBookTest() throws Exception{
        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789", 10000, 5000, false, 100);

        BookResponse response = new BookResponse(
                1L, "타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789",
                10000, 8000, false, LocalDateTime.now(), LocalDateTime.now(),
                BookStatus.ON_SALE, 10
        );

        given(bookService.createBook(any(BookCreateRequest.class))).willReturn(response);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.isbn").value(request.getIsbn()))
                .andExpect(jsonPath("$.author").value(request.getAuthor()));
    }

    @Test
    @DisplayName("POST /books - valid fail")
    void createBookValidationFailTest() throws Exception {
        BookCreateRequest request = new BookCreateRequest();
        request.setTitle("");

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("PATCH /books/{id}")
    void updateBookTest() throws Exception{
        Long id = book.getId();
        String updateTitle = "수정된 타이틀";

        BookUpdateRequest request = new BookUpdateRequest();
        request.setTitle(updateTitle);
        request.setStatus(BookStatus.SALE_END.name());

        book.setTitle(request.getTitle());
        book.setStatus(BookStatus.SALE_END);
        BookResponse response = BookResponse.of(book);

        given(bookService.updateBook(eq(id), any(BookUpdateRequest.class)))
                .willReturn(response);

        mockMvc.perform(patch("/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateTitle))
                .andExpect(jsonPath("$.status").value(BookStatus.SALE_END.name()));
    }

    @Test
    @DisplayName("DELETE /books/{id}")
    void deleteBookSuccessTest() throws Exception {
        Long id = book.getId();

        doNothing().when(bookService).deleteBook(id);

        mockMvc.perform(delete("/books/{id}", id))
                .andExpect(status().isNoContent());
    }
}
