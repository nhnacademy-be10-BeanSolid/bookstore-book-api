package com.nhnacademy.bookapi.booklike.controller;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import com.nhnacademy.bookapi.booklike.domain.BookLike;
import com.nhnacademy.bookapi.booklike.service.BookLikeService;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookLikeController.class)
class BookLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BookLikeService bookLikeService;

    Book book;
    BookLike bookLike;
    BookLike bookLike2;

    @BeforeEach
    void setUp() {
        book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        bookLike = new BookLike("user1", book);
        bookLike2 = new BookLike("user2", book);
    }

    @Test
    @DisplayName("좋아요 조회 - 도서 아이디")
    void getBookLikesByBookIdTest() throws Exception {
        Long bookId = book.getId();
        Pageable pageable = PageRequest.of(0, 10);
        List<BookLikeResponse> likes = List.of(
                BookLikeResponse.from(bookLike),
                BookLikeResponse.from(bookLike2)
        );
        Page<BookLikeResponse> page = new PageImpl<>(likes, pageable, likes.size());

        given(bookLikeService.getBookLikesByBookId(eq(bookId), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/books/{bookId}/bookLikes", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].userId").value("user1"))
                .andExpect(jsonPath("$.content[1].userId").value("user2"));
    }

    @Test
    @DisplayName("좋아요 조회 - 헤더")
    void getBookLikesByUserIdTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookLikeResponse> likes = List.of(BookLikeResponse.from(bookLike));
        Page<BookLikeResponse> page = new PageImpl<>(likes, pageable, likes.size());

        given(bookLikeService.getBookLikesByUserId(eq("user1"), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/users")
                        .header("X-USER-ID", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].userId").value("user1"));
    }

    @Test
    @DisplayName("좋아요 조회 - 헤더 없는 경우")
    void getBookLikesByUserIdExceptionTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookLikeResponse> likes = List.of(BookLikeResponse.from(bookLike));
        Page<BookLikeResponse> page = new PageImpl<>(likes, pageable, likes.size());

        given(bookLikeService.getBookLikesByUserId(eq("user1"), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/users"))
                .andExpect(status().isInternalServerError()); // 코드
    }

    @Test
    @DisplayName("좋아요 생성")
    void createBookLikeTest() throws Exception {
        Book newBook = new Book();
        ReflectionTestUtils.setField(newBook, "id", 2L);

        BookLikeResponse response = new BookLikeResponse(2L, LocalDateTime.now(), "user1", newBook.getId());

        given(bookLikeService.createBookLike(newBook.getId(), "user1")).willReturn(response);

        mockMvc.perform(post("/books/{bookId}/bookLikes", newBook.getId())
                        .header("X-USER-ID", "user1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value("2"))
                .andExpect(jsonPath("$.userId").value("user1"));
    }

    @Test
    @DisplayName("좋아요 생성 - 헤더 없는 경우")
    void createBookLikeExceptionTest() throws Exception {
        Book newBook = new Book();
        ReflectionTestUtils.setField(newBook, "id", 2L);

        BookLikeResponse response = new BookLikeResponse(2L, LocalDateTime.now(), "user1", newBook.getId());

        given(bookLikeService.createBookLike(newBook.getId(), "user1")).willReturn(response);

        mockMvc.perform(post("/books/{bookId}/bookLikes", newBook.getId()))
                .andExpect(status().isInternalServerError()); // 수정해야함
    }

    @Test
    @DisplayName("삭제 - 유저,도서 아이디")
    void deleteBookLikeByUserIdAndBookIdTest() throws Exception {
        doNothing().when(bookLikeService).deleteBookLikeByUserIdAndBookId("user1", book.getId());

        mockMvc.perform(delete("/books/{bookId}/bookLikes", book.getId())
                        .header("X-USER-ID", "user1"))
                .andExpect(status().isNoContent());

        verify(bookLikeService).deleteBookLikeByUserIdAndBookId("user1", book.getId());
    }

    @Test
    @DisplayName("유저,도서 아이디 삭제 - 헤더 없는 경우")
    void deleteBookLikeByUserIdAndBookIdExceptionTest() throws Exception {
        doNothing().when(bookLikeService).deleteBookLikeByUserIdAndBookId("user1", book.getId());

        mockMvc.perform(delete("/books/{bookId}/bookLikes", book.getId()))
                .andExpect(status().isInternalServerError());

    }
}
