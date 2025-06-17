package com.nhnacademy.bookapi.booklike.service;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.booklike.domain.request.BookLikeCreateRequest;
import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import com.nhnacademy.bookapi.booklike.domain.BookLike;
import com.nhnacademy.bookapi.booklike.exception.BookLikeAlreadyExistsException;
import com.nhnacademy.bookapi.booklike.exception.BookLikeNotFoundException;
import com.nhnacademy.bookapi.booklike.repository.BookLikeRepository;
import com.nhnacademy.bookapi.booklike.service.lmpl.BookLikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookLikeServiceImplTest {

    @Mock
    private BookLikeRepository bookLikeRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookLikeServiceImpl bookLikeService;

    Book book;
    BookLike bookLike;
    BookLike bookLike1;
    String userId;
    String userId1;

    @BeforeEach
    void setUp() {
        userId = "user1";
        userId1 = "user2";

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
        ReflectionTestUtils.setField(book, "id", 1L);
        bookLike = new BookLike(userId, book);
        bookLike1 = new BookLike(userId1, book);
    }

    @Test
    @DisplayName("좋아요 생성 성공")
    void createBookLikeSuccessTest() {
        BookLikeCreateRequest request = new BookLikeCreateRequest(userId);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookLikeRepository.existsByUserIdAndBookId(userId, book.getId())).thenReturn(false);

        BookLike savedBookLike = new BookLike(userId, book);
        when(bookLikeRepository.save(any(BookLike.class))).thenReturn(savedBookLike);
        when(bookLikeRepository.findBookLikeResponseById(any())).thenReturn(BookLikeResponse.of(savedBookLike));

        BookLikeResponse response = bookLikeService.createBookLike(book.getId(), request);

        assertThat(response).isNotNull();
        assertThat(response.bookId()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("좋아요 생성 실패 - 이미 좋아요 한 경우")
    void createBookLikeFailTest() {
        Long bookId = book.getId();
        BookLikeCreateRequest request = new BookLikeCreateRequest(userId);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookLikeRepository.existsByUserIdAndBookId(userId, book.getId())).thenReturn(true);

        assertThatThrownBy(() -> bookLikeService.createBookLike(bookId, request))
                .isInstanceOf(BookLikeAlreadyExistsException.class);
    }

    @Test
    @DisplayName("유저아이디로 좋아요 리스트 조회")
    void getBookLikesByUserIdTest() {
        BookLikeResponse response1 = BookLikeResponse.of(bookLike);
        BookLikeResponse response2 = BookLikeResponse.of(bookLike1);

        when(bookLikeRepository.findBookLikesByUserId(userId)).thenReturn(List.of(response1, response2));

        List<BookLikeResponse> bookLikes = bookLikeService.getBookLikesByUserId(userId);

        assertThat(bookLikes)
                .isNotNull()
                .hasSize(2);
        assertThat(bookLikes.get(0)).isEqualTo(BookLikeResponse.of(bookLike));
    }

    @Test
    @DisplayName("도서아이디로 좋아요 리스트 조회")
    void getBookLikesByBookIdTest() {
        BookLikeResponse response1 = BookLikeResponse.of(bookLike);
        BookLikeResponse response2 = BookLikeResponse.of(bookLike1);

        when(bookLikeRepository.existsByBookId(book.getId())).thenReturn(true);
        when(bookLikeRepository.findBookLikesByBookId(book.getId())).thenReturn(List.of(response1, response2));
        List<BookLikeResponse> bookLikes = bookLikeService.getBookLikesByBookId(book.getId());

        assertThat(bookLikes)
                .isNotNull()
                .hasSize(2);
        assertThat(bookLikes.get(0)).isEqualTo(BookLikeResponse.of(bookLike));
    }

    @Test
    @DisplayName("도서아이디에 해당하는 책이 없는 경우 조회")
    void getBookLikeByBookIdFailTest() {
        Long bookId = book.getId();
        when(bookLikeRepository.existsByBookId(book.getId())).thenReturn(false);

        assertThatThrownBy(() -> bookLikeService.getBookLikesByBookId(bookId))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("유저와 도서 아이디 가지고 좋아요 삭제 테스트")
    void deleteBookLikeByUserIdAndBookIdTest() {
        Long bookId = book.getId();
        when(bookLikeRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(true);

        doNothing().when(bookLikeRepository).deleteByUserIdAndBookId(userId, bookId);
        bookLikeService.deleteBookLikeByUserIdAndBookId(userId, bookId);

        verify(bookLikeRepository).deleteByUserIdAndBookId(userId, bookId);
    }

    @Test
    @DisplayName("유저와 도서 아이디에 해당하는 좋아요가 없어 실패")
    void deleteBookLikeByUserIdAndBookIdFailTest() {
        Long bookId = book.getId();
        when(bookLikeRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);

        assertThatThrownBy(() -> bookLikeService.deleteBookLikeByUserIdAndBookId(userId, bookId))
                .isInstanceOf(BookLikeNotFoundException.class);
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

        assertThatThrownBy(() -> bookLikeService.deleteBookLikeByBookId(bookId))
                .isInstanceOf(BookLikeNotFoundException.class);
    }
}
