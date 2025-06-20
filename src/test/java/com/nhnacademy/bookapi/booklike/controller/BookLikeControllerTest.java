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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

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
    String userId;

    @BeforeEach
    void setUp() {
        userId = "user";
        book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        bookLike = new BookLike(userId, book);
    }

    @Test
    @DisplayName("좋아요 조회 - 도서 아이디")
    void getBookLikesByBookIdTest() throws Exception {
        Long bookId = book.getId();
        BookLikeResponse response = BookLikeResponse.from(bookLike);

        given(bookLikeService.getBookLikesByBookId(bookId)).willReturn(List.of(response));

        mockMvc.perform(get("/books/{bookId}/bookLikes", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("좋아요 조회 - 헤더")
    void getBookLikesByUserIdTest() throws Exception {
        BookLikeResponse response = BookLikeResponse.from(bookLike);
        List<BookLikeResponse> responses = List.of(response);

        given(bookLikeService.getBookLikesByUserId(userId)).willReturn(responses);

        mockMvc.perform(get("/users")
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(userId));
    }

    @Test
    @DisplayName("좋아요 조회 - 헤더 없는 경우")
    void getBookLikesByUserIdExceptionTest() throws Exception {
        BookLikeResponse response = BookLikeResponse.from(bookLike);
        List<BookLikeResponse> responses = List.of(response);

        given(bookLikeService.getBookLikesByUserId(userId)).willReturn(responses);

        mockMvc.perform(get("/users"))
                .andExpect(status().isInternalServerError()); // 수정해야함
    }

    @Test
    @DisplayName("좋아요 조회 - 헤더 비어있는 경우")
    void getBookLikesByUserIdInvalidExceptionTest() throws Exception {
        BookLikeResponse response = BookLikeResponse.from(bookLike);
        List<BookLikeResponse> responses = List.of(response);

        given(bookLikeService.getBookLikesByUserId(userId)).willReturn(responses);

        mockMvc.perform(get("/users")
                        .header("X-USER-ID", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("좋아요 생성")
    void createBookLikeTest() throws Exception {
        Book newBook = new Book();
        ReflectionTestUtils.setField(newBook, "id", 2L);

        BookLikeResponse response = new BookLikeResponse(2L, LocalDateTime.now(), userId, newBook.getId());

        given(bookLikeService.createBookLike(newBook.getId(), userId)).willReturn(response);

        mockMvc.perform(post("/books/{bookId}/bookLikes", newBook.getId())
                        .header("X-USER-ID", userId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value("2"))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    @DisplayName("좋아요 생성 - 헤더 없는 경우")
    void createBookLikeExceptionTest() throws Exception {
        Book newBook = new Book();
        ReflectionTestUtils.setField(newBook, "id", 2L);

        BookLikeResponse response = new BookLikeResponse(2L, LocalDateTime.now(), userId, newBook.getId());

        given(bookLikeService.createBookLike(newBook.getId(), userId)).willReturn(response);

        mockMvc.perform(post("/books/{bookId}/bookLikes", newBook.getId()))
                .andExpect(status().isInternalServerError()); // 수정해야함
    }

    @Test
    @DisplayName("삭제 - 유저,도서 아이디")
    void deleteBookLikeByUserIdAndBookIdTest() throws Exception {
        doNothing().when(bookLikeService).deleteBookLikeByUserIdAndBookId(userId, book.getId());

        mockMvc.perform(delete("/books/{bookId}/bookLikes", book.getId())
                        .header("X-USER-ID", userId))
                .andExpect(status().isNoContent());

        verify(bookLikeService).deleteBookLikeByUserIdAndBookId(userId, book.getId());
    }

    @Test
    @DisplayName("유저,도서 아이디 삭제 - 헤더 없는 경우")
    void deleteBookLikeByUserIdAndBookIdExceptionTest() throws Exception {
        doNothing().when(bookLikeService).deleteBookLikeByUserIdAndBookId(userId, book.getId());

        mockMvc.perform(delete("/books/{bookId}/bookLikes", book.getId()))
                .andExpect(status().isInternalServerError());

    }
}
