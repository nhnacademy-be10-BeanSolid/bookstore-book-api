package com.nhnacademy.bookapi.booktag.service;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.nhnacademy.bookapi.booktag.exception.BookTagMapAlreadyExistsException;
import com.nhnacademy.bookapi.booktag.exception.BookTagMapNotFoundException;
import com.nhnacademy.bookapi.booktag.repository.BookTagRepository;
import com.nhnacademy.bookapi.booktag.service.impl.BookTagMapServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookTagMapServiceImplTest {

    @Mock
    BookTagRepository bookTagRepository;

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    private BookTagMapServiceImpl bookTagMapService;

    Book book;
    BookTag tag;

    @BeforeEach
    void setUp() {
        book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        tag = new BookTag("태그");
        ReflectionTestUtils.setField(tag, "tagId", 2L);

    }

    @Test
    @DisplayName("도서에 태그 추가")
    void createBookTagTest(){
        Long bookId = book.getId();
        Long tagId = tag.getTagId();

        BookTagMapCreateRequest request = new BookTagMapCreateRequest(tagId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookTagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        BookTagMapResponse response = new BookTagMapResponse(bookId, tagId);
        when(bookRepository.findBookTagMapResponseByBookIdAndTagId(bookId, tagId))
                .thenReturn(Optional.of(response));
        BookTagMapResponse result = bookTagMapService.createBookTag(bookId, request);

        assertThat(result.bookId()).isEqualTo(bookId);
        assertThat(result.tagId()).isEqualTo(tagId);
    }

    @Test
    @DisplayName("도서에 태그 추가 - 해당 태그가 존재하는 경우")
    void createBookTagExceptionTest(){
        Long bookId = book.getId();
        Long tagId = tag.getTagId();

        BookTagMapCreateRequest request = new BookTagMapCreateRequest(tagId);

        Set<BookTag> bookTags = new HashSet<>();
        bookTags.add(tag);
        book.setBookTags(bookTags);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookTagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        assertThatThrownBy(() -> bookTagMapService.createBookTag(bookId, request))
                .isInstanceOf(BookTagMapAlreadyExistsException.class);
    }

    @Test
    @DisplayName("도서 태그 삭제")
    void deleteBookTagTest(){
        Long bookId = book.getId();
        Long tagId = tag.getTagId();

        Set<BookTag> bookTags = new HashSet<>();
        bookTags.add(tag);
        book.setBookTags(bookTags);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookTagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        bookTagMapService.deleteBookTag(bookId, tagId);

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("도서 태그 삭제 - 태그가 존재하지 않은 경우")
    void deleteBookTagExceptionTest(){
        Long bookId = book.getId();
        Long tagId = tag.getTagId();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookTagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        assertThatThrownBy(() -> bookTagMapService.deleteBookTag(bookId, tagId))
                .isInstanceOf(BookTagMapNotFoundException.class);
    }
}
