package com.nhnacademy.bookapi.book.repository;

import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookOrderResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.common.config.QuerydslConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
class BookRepositoryImplTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void findBookResponseByIdTest() {
        Optional<BookResponse> result = bookRepository.findBookResponseById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().title()).isEqualTo("테스트책1");
        assertThat(result.get().author()).isEqualTo("김");
        assertThat(result.get().publisher()).isEqualTo("출판사");
    }

    @Test
    void findBookResponseByIdNotFoundTest() {
        Optional<BookResponse> result = bookRepository.findBookResponseById(99L);

        assertThat(result).isNotPresent();
    }

    @Test
    void findBookDetailResponseByBookIdTest() {
        Optional<BookDetailResponse> result = bookRepository.findBookDetailResponseByBookId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().title()).isEqualTo("테스트책1");
        assertThat(result.get().createAt()).isNotNull();
    }

    @Test
    void findBookDetailResponseByBookIdNotFoundTest() {
        Optional<BookDetailResponse> result = bookRepository.findBookDetailResponseByBookId(99L);

        assertThat(result).isNotPresent();
    }

    @Test
    void countBookCategoryByBookIdTest() {
        int result = bookRepository.countBookCategoryByBookId(1L);

        assertThat(result).isEqualTo(2);
    }

    @Test
    void countBookCategoryByBookIdNotFoundTest() {
        int result = bookRepository.countBookCategoryByBookId(3L);

        assertThat(result).isZero();
    }

    @Test
    void findBookOrderResponseByIdTest() {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<BookOrderResponse> result = bookRepository.findBookOrderResponsesById(ids);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);
    }

    @Test
    void findBookOrderResponseByIdNotFoundTest() {
        List<Long> ids = Arrays.asList(3L, 99L);
        List<BookOrderResponse> result = bookRepository.findBookOrderResponsesById(ids);

        assertThat(result).isEmpty();
    }
}