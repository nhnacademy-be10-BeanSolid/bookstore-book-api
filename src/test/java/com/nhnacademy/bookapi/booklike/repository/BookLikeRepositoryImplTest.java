package com.nhnacademy.bookapi.booklike.repository;

import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
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
        List<BookLikeResponse> result = bookLikeRepository.findBookLikeResponsesByBookId(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).bookId()).isEqualTo(1L);
        assertThat(result.get(1).bookId()).isEqualTo(1L);
    }

    @Test
    void findBookLikeResponseByUserIdTest() {
        List<BookLikeResponse> result = bookLikeRepository.findBookLikeResponsesByUserId("유저1");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().userId()).isEqualTo("유저1");
    }
}
