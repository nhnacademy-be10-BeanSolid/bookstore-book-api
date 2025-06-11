package com.nhnacademy.bookapi.bookLike;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.bookLike.controller.BookLikeController;
import com.nhnacademy.bookapi.bookLike.controller.request.BookLikeCreateRequest;
import com.nhnacademy.bookapi.bookLike.controller.response.BookLikeResponse;
import com.nhnacademy.bookapi.bookLike.domain.BookLike;
import com.nhnacademy.bookapi.bookLike.service.BookLikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookLikeController.class)
public class BookLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
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
    @DisplayName("GET /books/{bookId}/bookLikes")
    void getBookLikesByBookIdTest() throws Exception {
        BookLikeResponse response = BookLikeResponse.of(bookLike);

        given(bookLikeService.getBookLikesByBookId(book.getId())).willReturn(List.of(response));

        mockMvc.perform(get("/books/{bookId}/bookLikes", book.getId()))
//                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /users/{userId}/bookLikes")
    void getBookLikesByUserIdTest() throws Exception {
        List<BookLikeResponse> responses = bookLikeService.getBookLikesByUserId(userId);

        given(bookLikeService.getBookLikesByUserId(userId)).willReturn(responses);

        mockMvc.perform(get("/users/{userId}/bookLikes", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /books/{bookId}/bookLikes")
    void createBookLikeTest() throws Exception {
        Book newBook = new Book();
        ReflectionTestUtils.setField(newBook, "id", 2L);
        BookLikeCreateRequest request = new BookLikeCreateRequest(userId, newBook.getId());

        BookLikeResponse response = new BookLikeResponse(2L, LocalDateTime.now(), userId, newBook.getId());

        given(bookLikeService.createBookLike(any(BookLikeCreateRequest.class))).willReturn(response);

        mockMvc.perform(post("/books/{bookId}/bookLikes", newBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value("2"))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    @DisplayName("DELETE /books/{bookId}/bookLikes")
    void deleteBookLikeTest() throws Exception {
        doNothing().when(bookLikeService).deleteBookLikeByBookId(book.getId());

        mockMvc.perform(delete("/books/{bookId}/bookLikes", book.getId()))
                .andExpect(status().isNoContent());

        verify(bookLikeService).deleteBookLikeByBookId(book.getId());
    }

    @Test
    @DisplayName("DELETE /users/{userId}/bookLikes/{bookId}")
    void deleteBookLikeByUserIdAndBookIdTest() throws Exception {
        doNothing().when(bookLikeService).deleteBookLikeByUserIdAndBookId(userId, book.getId());

        mockMvc.perform(delete("/users/{userId}/bookLikes/{bookId}", userId, book.getId()))
                .andExpect(status().isNoContent());

        verify(bookLikeService).deleteBookLikeByUserIdAndBookId(userId, book.getId());
    }
}
