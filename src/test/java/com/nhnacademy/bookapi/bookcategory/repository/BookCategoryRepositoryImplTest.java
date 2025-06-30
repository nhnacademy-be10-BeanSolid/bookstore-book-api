package com.nhnacademy.bookapi.bookcategory.repository;

import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.config.QuerydslConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
class BookCategoryRepositoryImplTest {

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Test
    void findBookCategoryResponseByIdTest() {
        Optional<BookCategoryResponse> result = bookCategoryRepository.findBookCategoryResponseById(2L);

        assertThat(result).isPresent();
        assertThat(result.get().categoryId()).isEqualTo(2L);
        assertThat(result.get().parentId()).isEqualTo(1L);
        assertThat(result.get().parentCategoryName()).isEqualTo("소설");
        assertThat(result.get().categoryName()).isEqualTo("추리소설");
    }

    @Test
    void findBookCategoryResponseByIdNotFoundTest() {
        Optional<BookCategoryResponse> result = bookCategoryRepository.findBookCategoryResponseById(99L);

        assertThat(result).isNotPresent();
    }

    @Test
    void findAllBookCategoryResponseTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookCategoryResponse> result = bookCategoryRepository.findAllBookCategoryResponse(pageable);

        assertThat(result.getContent())
                .hasSize(3)
                .extracting(BookCategoryResponse::categoryName)
                .containsExactlyInAnyOrder("소설", "추리소설", "공포소설");
    }
}
