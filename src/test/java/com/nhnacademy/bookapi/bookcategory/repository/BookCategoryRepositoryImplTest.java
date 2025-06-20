package com.nhnacademy.bookapi.bookcategory.repository;

import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookCategoryRepositoryImplTest {

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Test
    void findBookCategoryResponseByIdTest() {
        Optional<BookCategoryResponse> result = bookCategoryRepository.findBookCategoryResponseById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().categoryId()).isEqualTo(1L);
        assertThat(result.get().categoryName()).isEqualTo("소설");
    }

    @Test
    void findBookCategoryResponseByIdNotFoundTest() {
        Optional<BookCategoryResponse> result = bookCategoryRepository.findBookCategoryResponseById(99L);

        assertThat(result).isNotPresent();
    }

    @Test
    void findAllBookCategoryResponseTest() {
        List<BookCategoryResponse> result = bookCategoryRepository.findAllBookCategoryResponse();

        assertThat(result)
                .isNotNull()
                .hasSize(3);
    }
}
