package com.nhnacademy.bookapi.document;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class BookDocumentTest {
    @Test
    void fromBook_returnsBookDocument() {
        Book book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        ReflectionTestUtils.setField(book, "title", "제목");
        ReflectionTestUtils.setField(book, "description", "설명");
        ReflectionTestUtils.setField(book, "author", "작가");
        ReflectionTestUtils.setField(book, "publisher", "출판사");
        ReflectionTestUtils.setField(book, "publishAt", LocalDate.of(2024, 1, 1));
        ReflectionTestUtils.setField(book, "salePrice", 12345);
        ReflectionTestUtils.setField(book, "viewCount", 100L);

        // 태그 설정
        BookTag tag1 = new BookTag("태그1");
        BookTag tag2 = new BookTag("태그2");

        Set<BookTag> tags = Set.of(tag1, tag2);
        ReflectionTestUtils.setField(book, "bookTags", tags);

        BookDocument doc = BookDocument.from(book);

        assertThat(doc).isNotNull();
        assertThat(doc.getId()).isEqualTo("1");
        assertThat(doc.getTitle()).isEqualTo("제목");
        assertThat(doc.getDescription()).isEqualTo("설명");
        assertThat(doc.getAuthor()).isEqualTo("작가");
        assertThat(doc.getPublisher()).isEqualTo("출판사");
        assertThat(doc.getPublishAt()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(doc.getSalePrice()).isEqualTo(12345);
        assertThat(doc.getViewCount()).isEqualTo(100L);
        assertThat(doc.getTags()).containsExactlyInAnyOrder("태그1", "태그2");
    }

}
