package com.nhnacademy.bookapi.booklike.repository;

import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import com.nhnacademy.bookapi.common.config.QuerydslConfig;
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
class BookLikeRepositoryImplTest {

    @Autowired
    private BookLikeRepository bookLikeRepository;

    @Test
    void findBookLikeResponseByIdTest() {
        Optional<BookLikeResponse> result = bookLikeRepository.findBookLikeResponseById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().bookLikeId()).isEqualTo(1L);
        assertThat(result.get().userId()).isEqualTo("유저1");
        assertThat(result.get().likedAt()).isNotNull();
    }

    @Test
    void findBookLikeResponseByIdNotFoundTest() {
        Optional<BookLikeResponse> result = bookLikeRepository.findBookLikeResponseById(99L);

        assertThat(result).isNotPresent();
    }

    @Test
    void findBookLikeResponseByBookIdTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookLikeResponse> result = bookLikeRepository.findBookLikeResponsesByBookId(1L, pageable);

        List<BookLikeResponse> content = result.getContent();

        assertThat(content).hasSize(2);
        assertThat(content.get(0).bookId()).isEqualTo(1L);
        assertThat(content.get(1).bookId()).isEqualTo(1L);
    }

    @Test
    void findBookLikeResponseByUserIdTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookLikeResponse> result = bookLikeRepository.findBookLikeResponsesByUserId("유저1", pageable);

        assertThat(result.getContent()).hasSize(2);
    }
}
