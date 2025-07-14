package com.nhnacademy.bookapi.booklike.service;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @BeforeEach
    void setUp() {

        BookCategory category = new BookCategory("소설", null);

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
                Set.of(category),
                0L
        );
        bookLike = new BookLike("user1", book);
        bookLike1 = new BookLike("user2", book);
    }

    @Test
    @DisplayName("좋아요 생성")
    void createBookLikeSuccessTest() {
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookLikeRepository.existsByUserIdAndBookId("user1", book.getId())).thenReturn(false);

        BookLike savedBookLike = new BookLike("user1", book);
        when(bookLikeRepository.save(any(BookLike.class))).thenReturn(savedBookLike);
        when(bookLikeRepository.findBookLikeResponseById(any())).thenReturn(Optional.of(BookLikeResponse.from(savedBookLike)));

        BookLikeResponse response = bookLikeService.createBookLike(book.getId(), "user1");

        assertThat(response).isNotNull();
        assertThat(response.bookId()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo("user1");
    }

    @Test
    @DisplayName("좋아요 생성 - 이미 좋아요 한 경우")
    void createBookLikeFailTest() {
        Long bookId = book.getId();

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookLikeRepository.existsByUserIdAndBookId("user1", book.getId())).thenReturn(true);

        assertThatThrownBy(() -> bookLikeService.createBookLike(bookId, "user1"))
                .isInstanceOf(BookLikeAlreadyExistsException.class);
    }

    @Test
    @DisplayName("유저아이디로 좋아요 리스트 조회")
    void getBookLikesByUserIdTest() {
        Pageable pageable = PageRequest.of(0, 10);
        BookLikeResponse response1 = BookLikeResponse.from(bookLike);
        Page<BookLikeResponse> result = new PageImpl<>(List.of(response1), pageable, 1);

        when(bookLikeRepository.findBookLikeResponsesByUserId("user1", pageable)).thenReturn(result);

        Page<BookLikeResponse> bookLikes = bookLikeService.getBookLikesByUserId("user1", pageable);

        assertThat(bookLikes)
                .hasSize(1);
        assertThat(bookLikes.getContent().getFirst()).isEqualTo(BookLikeResponse.from(bookLike));
    }

    @Test
    @DisplayName("도서아이디로 좋아요 리스트 조회")
    void getBookLikesByBookIdTest() {
        Long bookId = book.getId();
        Pageable pageable = PageRequest.of(0, 10);
        BookLikeResponse response1 = BookLikeResponse.from(bookLike);
        BookLikeResponse response2 = BookLikeResponse.from(bookLike1);
        Page<BookLikeResponse> response = new PageImpl<>(List.of(response1, response2), pageable, 2);

        when(bookLikeRepository.existsByBookId(book.getId())).thenReturn(true);
        when(bookLikeRepository.findBookLikeResponsesByBookId(bookId, pageable )).thenReturn(response);
        Page<BookLikeResponse> bookLikes = bookLikeService.getBookLikesByBookId(book.getId(), pageable);

        assertThat(bookLikes)
                .hasSize(2);
        assertThat(bookLikes.getContent().get(0)).isEqualTo(response1);
        assertThat(bookLikes.getContent().get(1)).isEqualTo(response2);
    }

    @Test
    @DisplayName("도서아이디에 해당하는 책이 없는 경우 조회")
    void getBookLikeByBookIdFailTest() {
        Long bookId = book.getId();

        when(bookLikeRepository.existsByBookId(book.getId())).thenReturn(false);

        assertThatThrownBy(() -> bookLikeService.getBookLikesByBookId(bookId, PageRequest.of(0, 10)))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("유저와 도서 아이디 가지고 좋아요 삭제 테스트")
    void deleteBookLikeByUserIdAndBookIdTest() {
        Long bookId = book.getId();
        when(bookLikeRepository.existsByUserIdAndBookId("user1", bookId)).thenReturn(true);

        doNothing().when(bookLikeRepository).deleteByUserIdAndBookId("user1", bookId);
        bookLikeService.deleteBookLikeByUserIdAndBookId("user1", bookId);

        verify(bookLikeRepository).deleteByUserIdAndBookId("user1", bookId);
    }

    @Test
    @DisplayName("삭제 - 유저/도서에 해당하는 좋아요가 없는 경우")
    void deleteBookLikeByUserIdAndBookIdFailTest() {
        Long bookId = book.getId();
        when(bookLikeRepository.existsByUserIdAndBookId("user1", bookId)).thenReturn(false);

        assertThatThrownBy(() -> bookLikeService.deleteBookLikeByUserIdAndBookId("user1", bookId))
                .isInstanceOf(BookLikeNotFoundException.class);
    }
}

