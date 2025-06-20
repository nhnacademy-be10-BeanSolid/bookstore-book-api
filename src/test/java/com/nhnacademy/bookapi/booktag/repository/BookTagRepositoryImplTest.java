package com.nhnacademy.bookapi.booktag.repository;

import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookTagRepositoryImplTest {

    @Autowired
    private BookTagRepository bookTagRepository;

    @Test
    void findBookTagResponseByIdTest() {
        Optional<BookTagResponse> result = bookTagRepository.findBookTagResponseById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().tagId()).isEqualTo(1L);
        assertThat(result.get().name()).isEqualTo("태그1");
    }

    @Test
    void findBookTagResponseByIdNotFoundTest() {
        Optional<BookTagResponse> result = bookTagRepository.findBookTagResponseById(99L);

        assertThat(result).isNotPresent();
    }

    @Test
    void findAllBookTagResponseTest() {
        List<BookTagResponse> result = bookTagRepository.findAllBookTagResponses();

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).name()).isEqualTo("태그1");
        assertThat(result.get(1).name()).isEqualTo("태그2");
    }
}
