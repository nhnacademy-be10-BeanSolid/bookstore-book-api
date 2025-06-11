package com.nhnacademy.bookapi.book;

import com.nhnacademy.bookapi.book.controller.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.controller.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.controller.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.exception.AuthorNotFoundException;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    Book book;

    @BeforeEach
    void setUp() {
        book = new Book("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test000000000", 10000, 5000, false, 100);
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
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getIsbn()).isEqualTo(request.getIsbn());
        assertThat(response.getAuthor()).isEqualTo(request.getAuthor());
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
    @DisplayName("ISBN 도서 찾기 성공")
    void getBookByIsbnSuccessTest() {
        String isbn = book.getIsbn();
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));

        BookResponse response = bookService.getBook(isbn);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(book.getTitle());
        assertThat(response.getIsbn()).isEqualTo(isbn);
        assertThat(response.getAuthor()).isEqualTo(book.getAuthor());
    }

    @Test
    @DisplayName("ISBN 도서 찾기 실패")
    void getBookByIsbnFailTest() {
        String isbn = book.getIsbn();
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> bookService.deleteBook(isbn));
    }

    @Test
    @DisplayName("작가 도서 찾기 성공")
    void getBooksByAuthorSuccessTest() {
        String author = book.getAuthor();
        when(bookRepository.existsByAuthor(author)).thenReturn(true);
        when(bookRepository.findByAuthor(author)).thenReturn(List.of(book));

        List<BookResponse> books = bookService.getBooksByAuthor(author);

        assertThat(books).isNotNull();
        assertThat(books.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("작가 도서 찾기 실패")
    void getBooksByAuthorFailTest() {
        String author = book.getAuthor();
        when(bookRepository.existsByAuthor(author)).thenReturn(false);

        assertThrows(AuthorNotFoundException.class,
                () -> bookService.getBooksByAuthor(author));
    }

    @Test
    @DisplayName("업데이트 성공")
    void updateBookSuccessTest() {
        String isbn = book.getIsbn();
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));

       BookUpdateRequest request = new BookUpdateRequest();
       request.setTitle("수정합니다");
       request.setStatus("sale_end");

       BookResponse response = bookService.updateBook(isbn, request);

       assertThat(response).isNotNull();
       assertThat(response.getTitle()).isEqualTo(request.getTitle());
       assertThat(response.getStatus()).isEqualTo(BookStatus.SALE_END);
    }

    @Test
    @DisplayName("업데이트 실패")
    void updateBookFailTest() {
        String isbn = "test111111111";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> bookService.updateBook(isbn, new BookUpdateRequest()));
    }

    @Test
    @DisplayName("삭제 성공")
    void deleteBookSuccessTest() {
        String isbn = "test000000000";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));

        doNothing().when(bookRepository).delete(book);
        bookService.deleteBook(isbn);

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("삭제 실패")
    void deleteBookFailTest() {
        String isbn = "test000000000";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> bookService.deleteBook(isbn));
    }


}
