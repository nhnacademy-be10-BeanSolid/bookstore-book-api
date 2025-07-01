package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookStockReduceRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookOrderResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookAlreadyExistsException;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.exception.BookNotSaleException;
import com.nhnacademy.bookapi.book.exception.InsufficientStockException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.book.service.impl.BookServiceImpl;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryNotFoundException;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.booklike.domain.BookLike;
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.document.repository.BookDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.LocalDateTime;
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
        bookCategory = new BookCategory(1L, "소설", null);

        book = new Book(
                1L,
                "타이틀",
                "설명",
                "목차",
                "작가",
                "출판사",
                LocalDate.of(2020, 10, 19),
                "test000000000",
                10000,
                5000,
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                BookStatus.ON_SALE,
                100,
                null,
                new HashSet<>(),
                Set.of(bookCategory),
                new HashSet<>()
        );
    }

    @Test
    void createBook_success() {
        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.of(2020,10,19) , "test000000000", 10000, 5000, false, 100, null, Set.of(1L));

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
    void createBook_alreadyExists() {
        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789", 10000, 5000, false, 100, null, new HashSet<>());

        when(bookRepository.existsByIsbn("test123456789")).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(BookAlreadyExistsException.class);
    }

    @Test
    void createBook_bookCategoryNotFound() {
        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789", 10000, 5000, false, 100, null, Set.of(99L));

        when(bookRepository.existsByIsbn("test123456789")).thenReturn(false);
        when(bookCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(BookCategoryNotFoundException.class);
    }

    @Test
    void getBookDetailResponseByBookId_success() {
        Long id = book.getId(); // 1L
        Set<BookLike> likedUsers = new HashSet<>();
        likedUsers.add(new BookLike("user", book));
        likedUsers.add(new BookLike("user2", book));
        book.setBookLikes(likedUsers);

        BookDetailResponse response = BookDetailResponse.from(book);

        when(bookRepository.findBookDetailResponseByBookId(id)).thenReturn(Optional.of(response));

        BookDetailResponse result = bookService.getBookDetailResponseByBookId(id);

        assertThat(result).isNotNull();
        assertThat(result.likedUsers())
                .hasSize(2)
                .containsExactlyInAnyOrder("user", "user2");
    }

    @Test
    void getBookDetailResponseByBookId_notFound() {
        when(bookRepository.findBookDetailResponseByBookId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookDetailResponseByBookId(999L))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void getAllBooksTest() {
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
                new HashSet<>(),
                Set.of(bookCategory),
                new HashSet<>()
        );
        BookResponse response1 = BookResponse.from(book);
        BookResponse response2 = BookResponse.from(book1);

        Pageable pageable = PageRequest.of(0, 4);

        when(bookRepository.findAllBookResponses(pageable))
                .thenReturn(new PageImpl<>(List.of(response1, response2), pageable, 2));

        Page<BookResponse> pageResult = bookService.getAllBooks(pageable);

        assertThat(pageResult).isNotNull();
        assertThat(pageResult).hasSize(2);
        assertThat(pageResult.getContent().get(0)).isEqualTo(response1);
        assertThat(pageResult.getContent().get(1)).isEqualTo(response2);
    }

    @Test
    void updateBook_success() {
        Set<BookLike> likedUsers = new HashSet<>();
        likedUsers.add(new BookLike("user", book));
        likedUsers.add(new BookLike("user2", book));
        book.setBookLikes(likedUsers);

        BookUpdateRequest request = new BookUpdateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.of(2020,10,19), 10000, 5000, true, BookStatus.SALE_END.toString(), 100);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        book.updateFrom(request);
        when(bookRepository.findBookDetailResponseByBookId(1L)).thenReturn(Optional.of(BookDetailResponse.from(book)));

        BookDetailResponse result = bookService.updateBook(1L, request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.wrappable()).isTrue();
        assertThat(result.status()).isEqualTo(BookStatus.SALE_END);
    }

    @Test
    void updateBook_notFound() {
        BookUpdateRequest request = new BookUpdateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.of(2020,10,19), 10000, 5000, true, BookStatus.SALE_END.toString(), 80);

        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(999L, request))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void deleteBook_success() {
        Long id = book.getId(); // 1L
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        doNothing().when(bookRepository).delete(book);
        doNothing().when(bookDocumentRepository).deleteById(String.valueOf(id));

        bookService.deleteBook(id);

        verify(bookDocumentRepository, times(1)).deleteById(String.valueOf(id));
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void deleteBook_notFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(999L))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void getBookOrderResponseByBookIds_success() {
        ReflectionTestUtils.setField(book, "status", BookStatus.ON_SALE);
        List<BookOrderResponse> responses = List.of(
                BookOrderResponse.from(book)
        );

        when(bookRepository.findBookOrderResponsesById(List.of(1L))).thenReturn(responses);

        List<BookOrderResponse> result = bookService.getBookOrderResponseByBookIds(List.of(1L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(1L);
    }

    @Test
    void getBookOrderResponseByBookIds_notSaleException() {
        ReflectionTestUtils.setField(book, "status", BookStatus.SALE_END);

        when(bookRepository.findBookOrderResponsesById(List.of(1L))).thenReturn(List.of());

        assertThatThrownBy(() -> bookService.getBookOrderResponseByBookIds(List.of(1L)))
                .isInstanceOf(BookNotSaleException.class);
    }

    @Test
    void updateBookStock_success() {
        List<BookStockReduceRequest> requests = List.of(
                new BookStockReduceRequest(1L, 40)
        );

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.updateBookStock(requests);

        assertThat(book.getStock()).isEqualTo(60);
    }

    @Test
    void updateBookStock_bookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        List<BookStockReduceRequest> requests = List.of(
                new BookStockReduceRequest(1L, 40)
        );

        assertThatThrownBy(() -> bookService.updateBookStock(requests))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void updateBookStock_insufficientStock() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        List<BookStockReduceRequest> requests = List.of(
                new BookStockReduceRequest(1L, 120) // 재고 부족 요청
        );

        assertThatThrownBy(() -> bookService.updateBookStock(requests))
                .isInstanceOf(InsufficientStockException.class);
    }
}
