package com.nhnacademy.bookapi.bookcategory.service;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryMapCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryMapAlreadyExistsException;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryMapCreateException;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryMapDeleteFailException;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryMapNotFoundException;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.bookcategory.service.impl.BookCategoryMapServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCategoryMapServiceImplTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    BookCategoryRepository bookCategoryRepository;

    @InjectMocks
    BookCategoryMapServiceImpl bookCategoryMapService;

    Book book;
    BookCategory category;

    @BeforeEach
    void setUp() {
        book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        category = new BookCategory("카테고리", null);
        ReflectionTestUtils.setField(category, "categoryId", 1L);
    }

    @Test
    @DisplayName("도서에 카테고리 추가")
    void createBookCategoryMapTest() {
        Long bookId = book.getId();
        Long categoryId = category.getCategoryId();

        BookCategoryMapCreateRequest request = new BookCategoryMapCreateRequest(categoryId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.countBookCategoryByBookId(bookId)).thenReturn(1);
        when(bookCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        BookCategoryResponse categoryResponse = new BookCategoryResponse(categoryId, "카테고리",
                null, null, LocalDateTime.now(), null);
        BookCategoryMapResponse response = new BookCategoryMapResponse(bookId, List.of(categoryResponse));
        when(bookCategoryRepository.findBookCategoryMapResponse(bookId))
                .thenReturn(response);

        BookCategoryMapResponse result = bookCategoryMapService.createBookCategoryMap(bookId, request);

        assertThat(result.bookId()).isEqualTo(bookId);
        assertThat(result.categories()).hasSize(1);
        assertThat(result.categories().getFirst().categoryId()).isEqualTo(categoryId);
        assertThat(result.categories().getFirst().categoryName()).isEqualTo(category.getName());
    }

    @Test
    @DisplayName("도서에 카테고리 추가 - 해당 도서에 카테고리가 10개 이상인 경우")
    void createBookCategoryMapExceptionTest() {
        Long bookId = book.getId();
        Long categoryId = category.getCategoryId();

        BookCategoryMapCreateRequest request = new BookCategoryMapCreateRequest(categoryId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.countBookCategoryByBookId(bookId)).thenReturn(10);

        assertThatThrownBy(() -> bookCategoryMapService.createBookCategoryMap(bookId, request))
                .isInstanceOf(BookCategoryMapCreateException.class);
    }

    @Test
    @DisplayName("도서에 카테고리 추가 - 해당 카테고리가 존재하는 경우")
    void createBookCategoryMapNotFoundExceptionTest() {
        Long bookId = book.getId();
        Long categoryId = category.getCategoryId();

        BookCategoryMapCreateRequest request = new BookCategoryMapCreateRequest(categoryId);

        Set<BookCategory> bookCategorySet = new HashSet<>();
        bookCategorySet.add(category);
        book.setBookCategories(bookCategorySet);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.countBookCategoryByBookId(bookId)).thenReturn(1);
        when(bookCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> bookCategoryMapService.createBookCategoryMap(bookId, request))
                .isInstanceOf(BookCategoryMapAlreadyExistsException.class);
    }

    @Test
    @DisplayName("도서에서 카테고리 삭제")
    void deleteCategoryMapTest() {
        Long bookId = book.getId();
        Long categoryId = category.getCategoryId();

        Set<BookCategory> bookCategorySet = new HashSet<>();
        bookCategorySet.add(category);
        bookCategorySet.add(new BookCategory("테스트", null));
        book.setBookCategories(bookCategorySet);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(bookRepository.countBookCategoryByBookId(bookId)).thenReturn(bookCategorySet.size());

        bookCategoryMapService.deleteCategoryMap(bookId, categoryId);

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("도서에서 카테고리 삭제 - 카테고리 최소 조건 만족하지 않은 경우")
    void deleteCategoryMapFailTest() {
        Long bookId = book.getId();
        Long categoryId = category.getCategoryId();

        Set<BookCategory> bookCategorySet = new HashSet<>();
        bookCategorySet.add(category);
        book.setBookCategories(bookCategorySet);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(bookRepository.countBookCategoryByBookId(bookId)).thenReturn(bookCategorySet.size());

        assertThatThrownBy(() -> bookCategoryMapService.deleteCategoryMap(bookId, categoryId))
                .isInstanceOf(BookCategoryMapDeleteFailException.class);
    }

    @Test
    @DisplayName("도서에서 카테고리 삭제 - 존재하지 않은 경우")
    void deleteBookCategoryMapExceptionTest() {
        Long bookId = book.getId();
        Long categoryId = category.getCategoryId();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> bookCategoryMapService.deleteCategoryMap(bookId, categoryId))
                .isInstanceOf(BookCategoryMapNotFoundException.class);
    }

}
