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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

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
        bookRepository.save(book);
    }

    @Test
    @DisplayName("추가 성공")
    void createBookSuccessTest() {
        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789", 10000, 5000, false, 100);

        when(bookRepository.existsByIsbn("test123456789")).thenReturn(false);
        when(bookRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse response = bookService.createBook(request);

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo(request.title());
        assertThat(response.isbn()).isEqualTo(request.isbn());
        assertThat(response.author()).isEqualTo(request.author());
    }

    @Test
    @DisplayName("추가 실패")
    void createBookFailTest() {
        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789", 10000, 5000, false, 100);

        when(bookRepository.existsByIsbn("test123456789")).thenReturn(true);

        assertThrows(BookAlreadyExistsException.class,
                () -> bookService.createBook(request)
        );
    }

    @Test
    @DisplayName("아이디로 도서 찾기 성공")
    void getBookDetailByBookIdSuccessTest() {
        Long id = book.getId();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        BookDetailResponse response = bookService.getBookDetailByBookId(id);

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo(book.getTitle());
        assertThat(response.isbn()).isEqualTo(book.getIsbn());
        assertThat(response.author()).isEqualTo(book.getAuthor());
    }

    @Test
    @DisplayName("아이디로 도서 찾기 실패")
    void getBookDetailByBookIdFailTest() {
        Long id = book.getId();

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> bookService.getBookDetailByBookId(id));
    }


    @Test
    @DisplayName("작가로 도서 찾기")
    void getBooksByAuthorSuccessTest() {
        String author = book.getAuthor();

        when(bookRepository.findByAuthor(author)).thenReturn(List.of(book));

        List<BookDetailResponse> books = bookService.getBooksByAuthor(author);

        assertThat(books).isNotNull();
        assertThat(books).hasSize(List.of(book).size());
    }

    @Test
    @DisplayName("출판사로 도서 찾기")
    void getBooksByPublisherTest() {
        String publisher = book.getPublisher();

        when(bookRepository.findByPublisher(publisher)).thenReturn(List.of(book));

        List<BookDetailResponse> books = bookService.getBooksByPublisher(publisher);

        assertThat(books).isNotNull();
        assertThat(books).hasSize(List.of(book).size());
    }

    @Test
    @DisplayName("업데이트 성공")
    void updateBookSuccessTest() {
        Long id = book.getId();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        BookUpdateRequest request = new BookUpdateRequest();
        request.setTitle("수정합니다");
        request.setStatus("sale_end");

        BookResponse response = bookService.updateBook(id, request);

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo(request.getTitle());
        assertThat(response.status()).isEqualTo(BookStatus.SALE_END);
    }

    @Test
    @DisplayName("업데이트 실패")
    void updateBookFailTest() {
        Long id = book.getId();
        BookUpdateRequest request = new BookUpdateRequest();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> bookService.updateBook(id, request));
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

        assertThrows(BookNotFoundException.class,
                () -> bookService.deleteBook(id));
    }
}
