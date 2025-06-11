package com.nhnacademy.bookapi.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.book.controller.BookController;
import com.nhnacademy.bookapi.book.controller.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.controller.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.controller.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    BookService bookService;

    Book book;

    @BeforeEach
    void setUp() {
        book = new Book("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test000000000", 10000, 5000, false, 100);
    }

    @Test
    @DisplayName("GET /books/{isbn}")
    void getBookTest() throws Exception{
        String isbn = book.getIsbn();
        BookResponse response = BookResponse.of(book);

        given(bookService.getBook(isbn)).willReturn(response);

        mockMvc.perform(get("/books/{isbn}", isbn)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("타이틀"))
                .andExpect(jsonPath("$.isbn").value(isbn))
                .andExpect(jsonPath("$.author").value("작가"));
    }

    @Test
    @DisplayName("GET /authors/{author}")
    void getAuthorTest() throws Exception{
        String author = book.getAuthor();
        BookResponse response = BookResponse.of(book);

        given(bookService.getBooksByAuthor(author)).willReturn(List.of(response));

        mockMvc.perform(get("/authors/{author}", author)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].author").value(author));
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
                .andExpect(jsonPath("$.title").value("타이틀"))
                .andExpect(jsonPath("$.isbn").value("test123456789"))
                .andExpect(jsonPath("$.author").value("작가"));
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
    @DisplayName("PUT /books/{isbn}")
    void updateBookTest() throws Exception{
        String isbn = "test123456789";
        String updateTitle = "수정된 타이틀";

        BookUpdateRequest request = new BookUpdateRequest();
        request.setTitle(updateTitle);
        request.setStatus(BookStatus.SALE_END.name());

        book.setTitle(request.getTitle());
        book.setStatus(BookStatus.SALE_END);
        BookResponse response = BookResponse.of(book);

        given(bookService.updateBook(eq(isbn), any(BookUpdateRequest.class)))
                .willReturn(response);

        mockMvc.perform(put("/books/{isbn}", isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateTitle))
                .andExpect(jsonPath("$.status").value(BookStatus.SALE_END.name()));
    }


    @Test
    @DisplayName("DELETE /books/{isbn}")
    void deleteBookSuccessTest() throws Exception {
        String isbn = "test123456789";

        doNothing().when(bookService).deleteBook(isbn);

        mockMvc.perform(delete("/books/{isbn}", isbn))
                .andExpect(status().isNoContent());
    }
}
