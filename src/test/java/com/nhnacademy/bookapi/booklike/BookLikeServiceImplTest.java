package com.nhnacademy.bookapi.booklike;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.booklike.controller.request.BookLikeCreateRequest;
import com.nhnacademy.bookapi.booklike.controller.response.BookLikeResponse;
import com.nhnacademy.bookapi.booklike.domain.BookLike;
import com.nhnacademy.bookapi.booklike.exception.BookLikeAlreadyExistsException;
import com.nhnacademy.bookapi.booklike.exception.BookLikeNotExistsException;
import com.nhnacademy.bookapi.booklike.repository.BookLikeRepository;
import com.nhnacademy.bookapi.booklike.service.lmpl.BookLikeServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookLikeServiceImplTest {

    @Mock
    private BookLikeRepository bookLikeRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookLikeServiceImpl bookLikeService;

    Book book;
    BookLike bookLike;
    String userId;

    @BeforeEach
    void setUp() {
        userId = "userId";
        book = new Book("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test000000000", 10000, 5000, false, 100);
        bookLike = new BookLike(userId, book);
    }

    @Test
    @DisplayName("좋아요 생성 성공")
    void createBookLikeSuccessTest() {
        BookLikeCreateRequest request = new BookLikeCreateRequest(userId, book.getId());

        when(bookRepository.findById(book.getId())).thenReturn(Optional.ofNullable(book));
        when(bookLikeRepository.existsByUserIdAndBookId(userId, book.getId())).thenReturn(false);

        BookLikeResponse response = bookLikeService.createBookLike(request);

        assertThat(response).isNotNull();
        assertThat(response.getBookId()).isEqualTo(book.getId());
        assertThat(response.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("좋아요 생성 실패")
    void createBookLikeFailTest() {
        BookLikeCreateRequest request = new BookLikeCreateRequest(userId, book.getId());

        when(bookRepository.findById(book.getId())).thenReturn(Optional.ofNullable(book));
        when(bookLikeRepository.existsByUserIdAndBookId(userId, book.getId())).thenReturn(true);

        assertThrows(BookLikeAlreadyExistsException.class,
                () -> bookLikeService.createBookLike(request));
    }

    @Test
    @DisplayName("유저아이디로 좋아요 리스트 조회")
    void getBookLikesByUserIdTest() {
        when(bookLikeRepository.getBookLikesByUserId(userId)).thenReturn(List.of(bookLike));
        List<BookLikeResponse> bookLikes = bookLikeService.getBookLikesByUserId(userId);

        assertThat(bookLikes).isNotNull();
        assertThat(bookLikes.size()).isEqualTo(1);
        assertThat(bookLikes.get(0)).isEqualTo(BookLikeResponse.of(bookLike));
    }

    @Test
    @DisplayName("도서아이디로 좋아요 리스트 조회")
    void getBookLikesByBookIdTest() {
        when(bookLikeRepository.existsByBookId(book.getId())).thenReturn(true);
        when(bookLikeRepository.getBookLikesByBookId(book.getId())).thenReturn(List.of(bookLike));
        List<BookLikeResponse> bookLikes = bookLikeService.getBookLikesByBookId(book.getId());

        assertThat(bookLikes).isNotNull();
        assertThat(bookLikes.size()).isEqualTo(1);
        assertThat(bookLikes.get(0)).isEqualTo(BookLikeResponse.of(bookLike));
    }

    @Test
    @DisplayName("도서아이디에 해당하는 책이 없는 경우에 조회")
    void getBookLikeByBookIdFailTest() {
        when(bookLikeRepository.existsByBookId(book.getId())).thenReturn(false);

        assertThrows(BookNotFoundException.class,
                () -> bookLikeService.getBookLikesByBookId(book.getId()));
    }

    @Test
    @DisplayName("유저정보를 가지고 좋아요 삭제 테스트")
    void deleteBookLikeByUserIdTest() {
        Long bookId = book.getId();
        when(bookLikeRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(true);

        doNothing().when(bookLikeRepository).deleteByUserIdAndBookId(userId, bookId);
        bookLikeService.deleteBookLikeByUserIdAndBookId(userId, bookId);

        verify(bookLikeRepository).deleteByUserIdAndBookId(userId, bookId);
    }

    @Test
    @DisplayName("유저와 도서 정보에 해당하는 좋아요가 없어 실패")
    void deleteBookLikeByUserIdTFailTest() {
        Long bookId = book.getId();
        when(bookLikeRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);

        assertThrows(BookLikeNotExistsException.class,
                () -> bookLikeService.deleteBookLikeByUserIdAndBookId(userId, bookId));
    }

    @Test
    @DisplayName("삭제 성공")
    void deleteBookLikeByBookIdSuccessTest() {
        Long bookId = book.getId();
        when(bookRepository.existsById(book.getId())).thenReturn(true);
        when(bookLikeRepository.existsByBookId(bookId)).thenReturn(true);

        doNothing().when(bookLikeRepository).deleteByBookId(bookId);
        bookLikeService.deleteBookLikeByBookId(bookId);

        verify(bookLikeRepository, times(1)).deleteByBookId(bookId);
    }

    @Test
    @DisplayName("삭제 실패 - 좋아요 존재하지 않은 경우")
    void deleteBookLikeByBookIdFailTestCase1() {
        Long bookId = book.getId();
        when(bookRepository.existsById(book.getId())).thenReturn(true);
        when(bookLikeRepository.existsByBookId(bookId)).thenReturn(false);

        assertThrows(BookLikeNotExistsException.class,
                () -> bookLikeService.deleteBookLikeByBookId(bookId));
    }
}
