package com.nhnacademy.bookapi.booktag.repository;

import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
import com.nhnacademy.bookapi.config.QuerydslConfig;
import com.querydsl.core.QueryFactory;
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
class BookTagRepositoryImplTest {

    @Autowired
    private BookTagRepository bookTagRepository;

    @Test
    void findBookTagResponseByIdTest() {
        Optional<BookTagResponse> result = bookTagRepository.findBookTagResponseById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().tagId()).isEqualTo(1L);
        assertThat(result.get().tagName()).isEqualTo("태그1");
    }

    @Test
    void findBookTagResponseByIdNotFoundTest() {
        Optional<BookTagResponse> result = bookTagRepository.findBookTagResponseById(99L);

        assertThat(result).isNotPresent();
    }

    @Test
    void findAllBookTagResponseTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookTagResponse> result = bookTagRepository.findAllBookTagResponses(pageable);

        assertThat(result.getContent())
                .hasSize(2)
                .extracting(BookTagResponse::tagName)
                .containsExactlyInAnyOrder("태그1", "태그2");
    }
}
