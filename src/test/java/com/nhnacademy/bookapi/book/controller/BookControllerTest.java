package com.nhnacademy.bookapi.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookSearchResponse;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.service.BookSearchApiService;
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
                .bookCategories(categories)
                .bookTags(tags)
                .bookLikes(new HashSet<>())
                .build();
        Set<BookLike> bookLikes = book.getBookLikes();
        bookLikes.add(new BookLike("user", book));
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
    @DisplayName("도서 상세정보 조회")
    void getBookTest() throws Exception{
        Long bookId = book.getId();
        BookDetailResponse response = BookDetailResponse.from(book);

        given(bookService.getBookDetailResponseByBookId(bookId)).willReturn(response);

        mockMvc.perform(get("/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("타이틀"))
                .andExpect(jsonPath("$.isbn").value("test000000000"))
                .andExpect(jsonPath("$.author").value("작가"))
                .andExpect(jsonPath("$.likedUsers").value(Matchers.hasItems("user")));
    }

    @Test
    @DisplayName("도서 전체 조회")
    void getAllBookResponseTest() throws Exception{
        Book book1 = Book.builder()
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
                .bookCategories(Set.of(category))
                .bookTags(Set.of(tag))
                .build();

        BookResponse response1 = BookResponse.from(book);
        BookResponse response2 = BookResponse.from(book1);

        Pageable page = PageRequest.of(0, 10);
        Page<BookResponse> pageResult = new PageImpl<>(List.of(response1, response2), page, 2);

        given(bookService.getAllBooks(page)).willReturn(pageResult);

        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("작가 전체 조회")
    void getAuthorTest() throws Exception{
        String author = book.getAuthor(); // 작가
        Book book1 = Book.builder()
                .title("타이틀")
                .description("설명")
                .toc("목차")
                .publisher("출판사")
                .author(author)
                .publishedDate(LocalDate.now())
                .isbn("test000000001")
                .originalPrice(10000)
                .salePrice(5000)
                .wrappable(false)
                .stock(100)
                .bookCategories(Set.of(category))
                .bookTags(Set.of(tag))
                .build();
        BookResponse response = BookResponse.from(book);
        BookResponse response1 = BookResponse.from(book1);

        Pageable page = PageRequest.of(0, 10);
        Page<BookResponse> pageResponse = new PageImpl<>(List.of(response1, response), page, 2);

        given(bookService.getBooksResponseByAuthor(author, page)).willReturn(pageResponse);

        mockMvc.perform(get("/authors/{author}", author)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].author").value("작가"));
    }

    @Test
    @DisplayName("출판사 전체 조회")
    void getBooksByPublisherTest() throws Exception{
        String publisher = book.getPublisher(); // 출판사
        Book book1 = Book.builder()
                .title("타이틀")
                .description("설명")
                .toc("목차")
                .publisher(publisher)
                .author("작가")
                .publishedDate(LocalDate.now())
                .isbn("test000000001")
                .originalPrice(10000)
                .salePrice(5000)
                .wrappable(false)
                .stock(100)
                .bookCategories(Set.of(category))
                .bookTags(Set.of(tag))
                .build();
        BookResponse response = BookResponse.from(book);
        BookResponse response1 = BookResponse.from(book1);

        Pageable page = PageRequest.of(0, 10);
        Page<BookResponse> pageResponse = new PageImpl<>(List.of(response1, response), page, 2);

        given(bookService.getBooksResponseByPublisher(publisher, page)).willReturn(pageResponse);

        mockMvc.perform(get("/publishers/{publisher}", publisher)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].publisher").value("출판사"));
    }

    @Test
    @DisplayName("태그로 도서 검색")
    void getBooksResponseByTagTest() throws Exception{
        String tagName = tag.getName(); // 태그

        BookResponse response = BookResponse.from(book);

        Pageable page = PageRequest.of(0, 10);
        Page<BookResponse> pageResponse = new PageImpl<>(List.of(response), page, 1);

        given(bookService.getBooksResponseByTag(tagName, page)).willReturn(pageResponse);

        mockMvc.perform(get("/search")
                        .param("tag", tagName)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].bookTags").value(Matchers.hasItem(tagName)));
    }

    @Test
    @DisplayName("도서 생성")
    void createBookTest() throws Exception{
        Set<Long> categoryIds = Set.of(1L);

        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test000000000", 10000, 5000, false, 100, categoryIds);

        given(bookService.createBook(any(BookCreateRequest.class))).willReturn(BookResponse.from(book));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("타이틀"))
                .andExpect(jsonPath("$.isbn").value("test000000000"))
                .andExpect(jsonPath("$.author").value("작가"))
                .andExpect(jsonPath("$.bookCategories").value(Matchers.hasItem("소설")));
    }

    @Test
    @DisplayName("도서 생성 - 유효성 검사 실패")
    void createBookValidationFailTest() throws Exception {
        Set<Long> categoryIds = Set.of(1L);

        // 재고량 -1로 지정
        BookCreateRequest badRequest = new BookCreateRequest(
                "타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789", 10000, 5000, false, -1, categoryIds);

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
        BookResponse response = BookResponse.from(book);

        given(bookService.updateBook(eq(id), any(BookUpdateRequest.class)))
                .willReturn(response);

        mockMvc.perform(patch("/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 타이틀"))
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
