package com.nhnacademy.bookapi.document.repository;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.response.SimpleBookResponse;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.document.repository.impl.CustomBookDocumentRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomBookDocumentRepositoryTest {

    ElasticsearchOperations elasticsearchOperations;
    BookRepository bookRepository;
    CustomBookDocumentRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        elasticsearchOperations = mock(ElasticsearchOperations.class);
        bookRepository = mock(BookRepository.class);
        repository = new CustomBookDocumentRepositoryImpl(elasticsearchOperations, bookRepository);
    }

    @Test
    void searchByKeyword_returnsPageOfSimpleBookResponse() {
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 4);

        SearchHit<BookDocument> hit1 = mock(SearchHit.class);
        BookDocument bookDocument1 = mock(BookDocument.class);
        when(hit1.getContent()).thenReturn(bookDocument1);
        when(bookDocument1.getId()).thenReturn(1L);

        SearchHit<BookDocument> hit2 = mock(SearchHit.class);
        BookDocument bookDocument2 = mock(BookDocument.class);
        when(hit2.getContent()).thenReturn(bookDocument2);
        when(bookDocument2.getId()).thenReturn(2L);

        // 검색 결과를 담고 있는 컨테이너
        SearchHits<BookDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(List.of(hit1, hit2));
        when(searchHits.getTotalHits()).thenReturn(2L);

        when(elasticsearchOperations.search((Query) any(), eq(BookDocument.class))).thenReturn(searchHits);

        Book book1 = new Book();
        ReflectionTestUtils.setField(book1, "id", 1L);
        ReflectionTestUtils.setField(book1, "title", "Title1");
        ReflectionTestUtils.setField(book1, "author", "Author1");
        ReflectionTestUtils.setField(book1, "salePrice", 1000);
        ReflectionTestUtils.setField(book1, "stock", 5);
        ReflectionTestUtils.setField(book1, "image", "img1");
        ReflectionTestUtils.setField(book1, "viewCount", 10L);

        Book book2 = new Book();
        ReflectionTestUtils.setField(book2, "id", 2L);
        ReflectionTestUtils.setField(book2, "title", "Title2");
        ReflectionTestUtils.setField(book2, "author", "Author2");
        ReflectionTestUtils.setField(book2, "salePrice", 2000);
        ReflectionTestUtils.setField(book2, "stock", 3);
        ReflectionTestUtils.setField(book2, "image", "img2");
        ReflectionTestUtils.setField(book2, "viewCount", 20L);

        when(bookRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(book1, book2));
        Page<SimpleBookResponse> result = repository.searchByKeyword(keyword, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).title()).isEqualTo("Title1");
        assertThat(result.getContent().get(1).title()).isEqualTo("Title2");

        verify(elasticsearchOperations, times(1)).search((Query) any(), eq(BookDocument.class));
        verify(bookRepository, times(1)).findAllById(List.of(1L, 2L));
    }
}
