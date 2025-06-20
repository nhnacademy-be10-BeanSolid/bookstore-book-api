package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookAlreadyExistsException;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.book.service.impl.BookServiceImpl;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.booklike.domain.BookLike;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    Book book;
    BookCategory bookCategory;

    @BeforeEach
    void setUp() {
        bookCategory = new BookCategory("소설", null);
        ReflectionTestUtils.setField(bookCategory,"categoryId", 1L);

        book = Book.builder()
                .title("타이틀")
                .description("설명")
                .toc("목차")
                .publisher("출판사")
                .author("작가")
                .publishedDate(LocalDate.of(2020,10,19))
                .isbn("test000000000")
                .originalPrice(10000)
                .salePrice(5000)
                .wrappable(false)
                .stock(100)
                .bookCategories(Set.of(bookCategory))
                .build();
        ReflectionTestUtils.setField(book,"id", 1L);
    }

    @Test
    @DisplayName("추가 성공")
    void createBookSuccessTest() {
        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.of(2020,10,19) , "test000000000", 10000, 5000, false, 100, Set.of(1L));

        when(bookRepository.existsByIsbn("test000000000")).thenReturn(false);
        when(bookCategoryRepository.findById(1L)).thenReturn(Optional.of(bookCategory));

        BookResponse bookResponse = BookResponse.from(book);

        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookRepository.findBookResponseById(1L)).thenReturn(Optional.of(bookResponse));

        BookResponse response = bookService.createBook(request);
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("타이틀");
        assertThat(response.isbn()).isEqualTo("test000000000");
        assertThat(response.bookCategories()).contains("소설");
        assertThat(response.publishedDate()).isEqualTo(LocalDate.of(2020,10,19));
    }

    @Test
    @DisplayName("추가 실패")
    void createBookFailTest() {
        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789", 10000, 5000, false, 100, new HashSet<>());

        when(bookRepository.existsByIsbn("test123456789")).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(BookAlreadyExistsException.class);
    }

    @Test
    @DisplayName("일반 정보 조회")
    void getBookResponseByBookIdSuccessTest() {
        Long id = book.getId();

        BookResponse response = BookResponse.from(book);

        when(bookRepository.findBookResponseById(id)).thenReturn(Optional.of(response));

        BookResponse actualResponse = bookService.getBookResponseByBookId(id);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.title()).isEqualTo("타이틀");
        assertThat(actualResponse.isbn()).isEqualTo("test000000000");
        assertThat(actualResponse.author()).isEqualTo("작가");
    }

    @Test
    @DisplayName("조회 실패")
    void getBookResponseByBookIdFailTest() {
        Long id = book.getId();

        when(bookRepository.findBookResponseById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookResponseByBookId(id))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("상세 정보 조회")
    void getBookDetailResponseByBookIdSuccessTest() {
        Long id = book.getId();
        Set<BookLike> likedUsers = new HashSet<>();
        likedUsers.add(new BookLike("user", book));
        book.setBookLikes(likedUsers);

        BookDetailResponse response = BookDetailResponse.from(book);

        when(bookRepository.findBookDetailResponseByBookId(id)).thenReturn(Optional.of(response));

        BookDetailResponse actualResponse = bookService.getBookDetailResponseByBookId(id);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.likedUsers()).hasSize(1);
    }

    @Test
    @DisplayName("조회 실패")
    void getBookDetailResponseByBookIdFailTest() {
        Long id = book.getId();

        when(bookRepository.findBookDetailResponseByBookId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookDetailResponseByBookId(id))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("전체 도서 검색")
    void getAllBooksTest() {
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
                .build();
        Book book2 = Book.builder()
                .title("타이틀")
                .description("설명")
                .toc("목차")
                .publisher("출판사")
                .author("작가")
                .publishedDate(LocalDate.now())
                .isbn("test00000001")
                .originalPrice(10000)
                .salePrice(5000)
                .wrappable(false)
                .stock(100)
                .build();
        BookResponse response1 = BookResponse.from(book);
        BookResponse response2 = BookResponse.from(book1);
        BookResponse response3 = BookResponse.from(book2);

        Pageable pageable = PageRequest.of(0, 9);

        when(bookRepository.findAllBookResponses(pageable))
                .thenReturn(new PageImpl<>(List.of(response1, response2, response3), pageable, 3));

        Page<BookResponse> pageResult = bookService.getAllBooks(pageable);

        assertThat(pageResult).isNotNull();
        assertThat(pageResult).hasSize(3);
        assertThat(pageResult.getContent().get(0)).isEqualTo(response1);
        assertThat(pageResult.getContent().get(1)).isEqualTo(response2);
        assertThat(pageResult.getContent().get(2)).isEqualTo(response3);
    }

    @Test
    @DisplayName("작가로 도서 검색")
    void getBooksResponseByAuthorTest() {
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
        BookResponse response = BookResponse.from(book);
        BookResponse response1 = BookResponse.from(book1);

        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findBookResponsesByAuthor(author, pageable))
                .thenReturn(new PageImpl<>(List.of(response, response1), pageable, 2));

        Page<BookResponse> pageResult = bookService.getBooksResponseByAuthor(author, pageable);

        assertThat(pageResult).isNotNull();
        assertThat(pageResult.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("출판사로 도서 검색")
    void getBooksResponseByPublisherTest() {
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
        BookResponse response = BookResponse.from(book);
        BookResponse response1 = BookResponse.from(book1);

        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findBookResponseByPublisher(publisher, pageable))
                .thenReturn(new PageImpl<>(List.of(response, response1), pageable, 2));

        Page<BookResponse> pageResult = bookService.getBooksResponseByPublisher(publisher, pageable);

        assertThat(pageResult).isNotNull();
        assertThat(pageResult.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("태그로 도서 검색")
    void getBooksResponseByTagTest() {
        String tagName = "existTag";

        BookResponse response = BookResponse.from(book);

        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findBookResponseByTag(tagName, pageable))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        Page<BookResponse> pageResult = bookService.getBooksResponseByTag(tagName, pageable);

        assertThat(pageResult).isNotNull();
        assertThat(pageResult.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("제목으로 도서 검색")
    void getBooksResponseByTitleTest() {
        String title = "타이틀";

        Book book1 = Book.builder()
                .title("타이틀123")
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
        BookResponse response = BookResponse.from(book);
        BookResponse response1 = BookResponse.from(book1);

        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findBookResponseByTitle(title, pageable))
                .thenReturn(new PageImpl<>(List.of(response, response1), pageable, 2));

        Page<BookResponse> pageResult = bookService.getBookResponseByTitle(title, pageable);
        BookResponse result = pageResult.getContent().get(0);
        BookResponse result1 = pageResult.getContent().get(1);

        assertThat(pageResult).isNotNull();
        assertThat(pageResult.getContent()).hasSize(2);
        assertThat(result.title()).contains("타이틀");
        assertThat(result1.title()).contains("타이틀");
    }

    @Test
    @DisplayName("설명으로 도서 검색")
    void getBooksResponseByDescriptionTest() {
        String description = "설명";

        BookResponse response = BookResponse.from(book);

        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findBookResponseByDescription(description, pageable))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        Page<BookResponse> pageResult = bookService.getBookResponseByDescription(description, pageable);
        BookResponse result = pageResult.getContent().getFirst();

        assertThat(result).isNotNull();
        assertThat(result.description()).contains("설명");
    }

    @Test
    @DisplayName("업데이트 성공")
    void updateBookSuccessTest() {
        Long id = book.getId();
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        BookUpdateRequest request = new BookUpdateRequest();
        request.setTitle("수정합니다");
        request.setStatus("sale_end");

        book.setTitle(request.getTitle());
        book.setStatus(BookStatus.from(request.getStatus()));

        BookResponse response = BookResponse.from(book);
        when(bookRepository.findBookResponseById(id)).thenReturn(Optional.of(response));

        BookResponse actualResponse = bookService.updateBook(id, request);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.title()).isEqualTo("수정합니다");
        assertThat(actualResponse.status()).isEqualTo(BookStatus.SALE_END);
    }

    @Test
    @DisplayName("업데이트 실패")
    void updateBookFailTest() {
        Long id = book.getId();
        BookUpdateRequest request = new BookUpdateRequest();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(id, request))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("삭제 성공")
    void deleteBookSuccessTest() {
        Long id = book.getId();
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        doNothing().when(bookRepository).delete(book);
        bookService.deleteBook(id);

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("삭제 실패")
    void deleteBookFailTest() {
        Long id = book.getId();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(id))
                .isInstanceOf(BookNotFoundException.class);
    }
}
