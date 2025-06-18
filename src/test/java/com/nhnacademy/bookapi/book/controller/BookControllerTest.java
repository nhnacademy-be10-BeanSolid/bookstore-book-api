package com.nhnacademy.bookapi.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.response.BookSearchResponse;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.service.BookSearchApiService;
import com.nhnacademy.bookapi.book.service.BookService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    BookTag tag;

    @BeforeEach
    void setUp() {
        tag = new BookTag(1L, "Test");
        Set<BookTag> tagSet = new HashSet<>();
        tagSet.add(tag);

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
                .bookTags(tagSet)
                .build();
        ReflectionTestUtils.setField(book, "id", 1L);
    }

    @Test
    @DisplayName("도서 검색 api 테스트")
    void bookServiceTest() throws Exception {
        String query = "포켓몬스터";

        given(bookSearchService.searchBook(query, 1)).willReturn(new BookSearchResponse());

        mockMvc.perform(get("/books-search?query=" + query))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("도서 id 단건 조회")
    void getBookTest() throws Exception{
        Long bookId = book.getId();
        BookResponse response = BookResponse.of(book);

        given(bookService.getBookResponseByBookId(bookId)).willReturn(response);

        mockMvc.perform(get("/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(response.title()))
                .andExpect(jsonPath("$.isbn").value(response.isbn()))
                .andExpect(jsonPath("$.author").value(response.author()));
    }

    @Test
    @DisplayName("작가 전체 조회")
    void getAuthorTest() throws Exception{
        String author = book.getAuthor();
        Book book1 = Book.builder()
                .title("타이틀")
                .description("설명")
                .toc("목차")
                .publisher("출판사")
                .author(author)
                .publishedDate(LocalDate.now())
                .isbn("test000000000")
                .originalPrice(10000)
                .salePrice(5000)
                .wrappable(false)
                .stock(100)
                .build();
        BookResponse response = BookResponse.of(book);
        BookResponse response1 = BookResponse.of(book1);

        Pageable page = PageRequest.of(0, 10);
        Page<BookResponse> pageResponse = new PageImpl<>(List.of(response1, response), page, 2);

        given(bookService.getBooksResponseByAuthor(author, page)).willReturn(pageResponse);

        mockMvc.perform(get("/authors/{author}", author)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].author").value(author));
    }

    @Test
    @DisplayName("출판사 전체 조회")
    void getBooksByPublisherTest() throws Exception{
        String publisher = book.getPublisher();
        Book book1 = Book.builder()
                .title("타이틀")
                .description("설명")
                .toc("목차")
                .publisher(publisher)
                .author("작가")
                .publishedDate(LocalDate.now())
                .isbn("test000000000")
                .originalPrice(10000)
                .salePrice(5000)
                .wrappable(false)
                .stock(100)
                .build();
        BookResponse response = BookResponse.of(book);
        BookResponse response1 = BookResponse.of(book1);

        Pageable page = PageRequest.of(0, 10);
        Page<BookResponse> pageResponse = new PageImpl<>(List.of(response1, response), page, 2);

        given(bookService.getBooksResponseByPublisher(publisher, page)).willReturn(pageResponse);

        mockMvc.perform(get("/publishers/{publisher}", publisher)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].publisher").value(publisher));
    }

    @Test
    @DisplayName("태그로 도서 검색")
    void getBooksResponseByTagTest() throws Exception{
        String tagName = tag.getName();

        BookResponse response = BookResponse.of(book);

        Pageable page = PageRequest.of(0, 10);
        Page<BookResponse> pageResponse = new PageImpl<>(List.of(response), page, 1);

        given(bookService.getBooksResponseByTag(tagName, page)).willReturn(pageResponse);

        mockMvc.perform(get("/search")
                        .param("tag", tagName)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].bookTags").value(Matchers.hasItem(tagName)));
    }

    @Test
    @DisplayName("도서 생성")
    void createBookTest() throws Exception{
        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test000000000", 10000, 5000, false, 100);

        given(bookService.createBook(any(BookCreateRequest.class))).willReturn(BookResponse.of(book));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(request.title()))
                .andExpect(jsonPath("$.isbn").value(request.isbn()))
                .andExpect(jsonPath("$.author").value(request.author()));
    }

    @Test
    @DisplayName("도서 생성 - 유효성 검사 실패")
    void createBookValidationFailTest() throws Exception {
        // 재고량 -1로 지정
        BookCreateRequest badRequest = new BookCreateRequest(
                "타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789", 10000, 5000, false, -1);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("수정")
    void updateBookTest() throws Exception{
        Long id = book.getId();
        String updateTitle = "수정된 타이틀";

        BookUpdateRequest updateRequest = new BookUpdateRequest();
        updateRequest.setTitle(updateTitle);
        updateRequest.setStatus(BookStatus.SALE_END.name());

        book.setTitle(updateRequest.getTitle());
        book.setStatus(BookStatus.SALE_END);
        BookResponse response = BookResponse.of(book);

        given(bookService.updateBook(eq(id), any(BookUpdateRequest.class)))
                .willReturn(response);

        mockMvc.perform(patch("/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateTitle))
                .andExpect(jsonPath("$.status").value(BookStatus.SALE_END.name()));
    }

    @Test
    @DisplayName("수정 - 유효성 검사 실패")
    void updateBookValidFailTest() throws Exception{
        BookUpdateRequest badRequest = new BookUpdateRequest();
        badRequest.setIsbn("1234567236827189317");

        mockMvc.perform(patch("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("삭제")
    void deleteBookSuccessTest() throws Exception {
        Long id = book.getId();

        doNothing().when(bookService).deleteBook(id);

        mockMvc.perform(delete("/books/{id}", id))
                .andExpect(status().isNoContent());
    }

}
