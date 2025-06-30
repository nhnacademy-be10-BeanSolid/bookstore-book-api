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
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.document.repository.BookDocumentRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookCategoryRepository bookCategoryRepository;
    @Mock
    private BookDocumentRepository bookDocumentRepository;

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
        when(bookDocumentRepository.save(any(BookDocument.class))).thenReturn(BookDocument.from(book));

        BookResponse response = bookService.createBook(request);
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("타이틀");
        assertThat(response.isbn()).isEqualTo("test000000000");
        assertThat(response.bookCategories()).contains("소설");
        assertThat(response.publishAt()).isEqualTo(LocalDate.of(2020,10,19));
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
    @DisplayName("삭제 성공")
    void deleteBookSuccessTest() {
        Long id = book.getId();
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        doNothing().when(bookRepository).delete(book);
        doNothing().when(bookDocumentRepository).deleteById(String.valueOf(id));

        bookService.deleteBook(id);

        verify(bookDocumentRepository, times(1)).deleteById(String.valueOf(id));
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
