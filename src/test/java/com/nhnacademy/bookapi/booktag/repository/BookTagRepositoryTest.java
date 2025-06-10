package com.nhnacademy.bookapi.booktag.repository;

import com.nhnacademy.bookapi.booktag.domain.BookTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookTagRepositoryTest {
    @Autowired
    BookTagRepository bookTagRepository;

    @Test
    @DisplayName("BookTag 이름 존재 여부 확인")
    void existsBookTagByNameTest() {
        // given
        BookTag tag = new BookTag("Spring");
        bookTagRepository.save(tag);

        // when
        boolean exists = bookTagRepository.existsBookTagByName("Spring");
        boolean notExists = bookTagRepository.existsBookTagByName("Nissan");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}