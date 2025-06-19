package com.nhnacademy.bookapi.book.repository;

import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.repository.impl.CustomBookRepositoryImpl;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CustomBookRepositoryImplTest {

    @Autowired
    private CustomBookRepositoryImpl customBookRepository;

    @Test
    void findBookResponseByIdTest() {
        Optional<BookResponse> result = customBookRepository.findBookResponseById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().title()).isEqualTo("테스트책1");
        assertThat(result.get().author()).isEqualTo("김");
        assertThat(result.get().publisher()).isEqualTo("출판사");
    }

    @Test
    void findBookResponseByIdNotFoundTest() {
        Optional<BookResponse> result = customBookRepository.findBookResponseById(3L);

        assertThat(result).isNotPresent();
    }

    @Test
    void findBookDetailResponseByBookIdTest() {
        Optional<BookDetailResponse> result = customBookRepository.findBookDetailResponseByBookId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().title()).isEqualTo("테스트책1");
        assertThat(result.get().likedUsers()).hasSize(2);
    }

    @Test
    void findBookDetailResponseByBookIdNotFoundTest() {
        Optional<BookDetailResponse> result = customBookRepository.findBookDetailResponseByBookId(3L);

        assertThat(result).isNotPresent();
    }

    @Test
    void findBookResponseByAuthorTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookResponse> result = customBookRepository.findBookResponsesByAuthor("김", pageable);

        BookResponse first = result.getContent().get(0);
        BookResponse second = result.getContent().get(1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(first.author()).isEqualTo("김");
        assertThat(second.author()).isEqualTo("김");
    }

    @Test
    void findBookResponseByPublisherTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookResponse> result = customBookRepository.findBookResponseByPublisher("출판사", pageable);

        BookResponse first = result.getContent().get(0);
        BookResponse second = result.getContent().get(1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(first.publisher()).isEqualTo("출판사");
        assertThat(second.publisher()).isEqualTo("출판사");
    }

    @Test
    void findBookResponseByTitleTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookResponse> result = customBookRepository.findBookResponseByTitle("테스트책1", pageable);

        BookResponse first = result.getContent().getFirst();

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(first.title()).contains("테스트책1");
    }

    @Test
    void findBookResponseByTitleReturnEmptyPageTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookResponse> result = customBookRepository.findBookResponseByTitle("", pageable);

        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    void findBookResponseByDescriptionTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookResponse> result = customBookRepository.findBookResponseByDescription("설명", pageable);

        BookResponse first = result.getContent().get(0);
        BookResponse second = result.getContent().get(1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(first.description()).contains("설명");
        assertThat(second.description()).contains("설명");
    }

    @Test
    void findBookResponseByDescriptionReturnEmptyPageTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookResponse> result = customBookRepository.findBookResponseByDescription("", pageable);

        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    void findBookTagMapResponseByBookIdAndTagIdTest() {
        Optional<BookTagMapResponse> result = customBookRepository.findBookTagMapResponseByBookIdAndTagId(1L, 1L);

        assertThat(result).isPresent();
        assertThat(result.get().bookId()).isEqualTo(1L);
        assertThat(result.get().tagId()).isEqualTo(1L);
    }

    @Test
    void findBookTagMapResponseByBookIdAndTagIdNotFoundTest() {
        Optional<BookTagMapResponse> result = customBookRepository.findBookTagMapResponseByBookIdAndTagId(3L, 1L);

        assertThat(result).isNotPresent();
    }

    @Test
    void findBookCategoryResponseByBookIdAndCategoryIdTest() {
        Optional<BookCategoryMapResponse> result = customBookRepository.findBookCategoryMapResponseByBookIdAndCategoryId(1L, 2L);

        assertThat(result).isPresent();
        assertThat(result.get().bookId()).isEqualTo(1L);
        assertThat(result.get().categoryId()).isEqualTo(2L);
    }

    @Test
    void findBookCategoryResponseByBookIdAndCategoryIdNotFoundTest() {
        Optional<BookCategoryMapResponse> result = customBookRepository.findBookCategoryMapResponseByBookIdAndCategoryId(3L, 2L);

        assertThat(result).isNotPresent();
    }

    @Test
    void countBookCategoryByBookIdTest() {
        int result = customBookRepository.countBookCategoryByBookId(1L);

        assertThat(result).isEqualTo(2);
    }

    @Test
    void countBookCategoryByBookIdNotFoundTest() {
        int result = customBookRepository.countBookCategoryByBookId(3L);

        assertThat(result).isEqualTo(0);
    }

    @Test
    void findBookResponseByTagTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookResponse> result = customBookRepository.findBookResponseByTag("태그1", pageable);
        BookResponse first = result.getContent().get(0);
        BookResponse second = result.getContent().get(1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(first.bookTags()).contains("태그1");
        assertThat(second.bookTags()).contains("태그1");
    }

    @Test
    void findBookResponseByTagReturnEmptyPageTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookResponse> result = customBookRepository.findBookResponseByTag("", pageable);

        assertThat(result.getContent()).hasSize(0);
    }
}

