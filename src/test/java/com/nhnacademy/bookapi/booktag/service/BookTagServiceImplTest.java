package com.nhnacademy.bookapi.booktag.service;

import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.exception.BookTagAlreadyExistsException;
import com.nhnacademy.bookapi.booktag.exception.BookTagNotFoundException;
import com.nhnacademy.bookapi.booktag.repository.BookTagRepository;
import com.nhnacademy.bookapi.booktag.service.impl.BookTagServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookTagServiceImplTest {

    @Mock
    BookTagRepository bookTagRepository;

    @InjectMocks
    BookTagServiceImpl bookTagService;

    @Test
    @DisplayName("getBookTags - 전체 조회")
    void getBookTags() {
        when(bookTagRepository.findAll()).thenReturn(List.of(new BookTag(1L, "tag1"), new BookTag(2L, "tag2")));

        List<BookTag> tags = bookTagService.getBookTags();

        assertThat(tags).hasSize(2);
        verify(bookTagRepository).findAll();
    }

    @Test
    @DisplayName("getBookTag - 존재하는 태그 조회")
    void getBookTag_found() {
        BookTag tag = new BookTag(1L, "tag1");
        when(bookTagRepository.findById(1L)).thenReturn(Optional.of(tag));

        BookTag result = bookTagService.getBookTag(1L);

        assertThat(result).isEqualTo(tag);
    }

    @Test
    @DisplayName("createBookTag - 중복 이름 예외")
    void createBookTag_duplicateName() {
        when(bookTagRepository.existsBookTagByName("tag1")).thenReturn(true);

        BookTag tag = new BookTag(null, "tag1");
        assertThatThrownBy(() -> bookTagService.createBookTag(tag))
                .isInstanceOf(BookTagAlreadyExistsException.class);
    }

    @Test
    @DisplayName("createBookTag - 정상 생성")
    void createBookTag_success() {
        when(bookTagRepository.existsBookTagByName("tag1")).thenReturn(false);
        BookTag tag = new BookTag(null, "tag1");
        when(bookTagRepository.save(tag)).thenReturn(new BookTag(1L, "tag1"));

        BookTag saved = bookTagService.createBookTag(tag);

        assertThat(saved.getName()).isEqualTo("tag1");
        verify(bookTagRepository).save(tag);
    }

    @Test
    @DisplayName("updateBookTag - 중복 이름 예외")
    void updateBookTag_duplicateName() {
        BookTag tag = new BookTag(1L, "tag");

        when(bookTagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(bookTagRepository.existsBookTagByName("tag1")).thenReturn(true);

        BookTag updateTag = new BookTag(tag.getTagId(), "tag1");
        assertThatThrownBy(() -> bookTagService.updateBookTag(updateTag))
                .isInstanceOf(BookTagAlreadyExistsException.class);
    }

    @Test
    @DisplayName("updateBookTag - 정상 업데이트")
    void updateBookTag_success() {
        BookTag tag = new BookTag(1L, "tag");

        when(bookTagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(bookTagRepository.existsBookTagByName("tag1")).thenReturn(false);

        BookTag updateTag = new BookTag(tag.getTagId(), "tag1");
        BookTag updated = bookTagService.updateBookTag(updateTag);

        assertThat(updated.getName()).isEqualTo("tag1");
    }

    @Test
    @DisplayName("deleteBookTag - 존재하지 않을 때 예외")
    void deleteBookTag_notFound() {
        when(bookTagRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> bookTagService.deleteBookTag(1L))
                .isInstanceOf(BookTagNotFoundException.class);
    }

    @Test
    @DisplayName("deleteBookTag - 정상 삭제")
    void deleteBookTag_success() {
        when(bookTagRepository.existsById(1L)).thenReturn(true);

        bookTagService.deleteBookTag(1L);

        verify(bookTagRepository).deleteById(1L);
    }

    @Test
    @DisplayName("existsBookTag - id로 존재 여부")
    void existsBookTagById() {
        when(bookTagRepository.existsById(1L)).thenReturn(true);

        assertThat(bookTagService.existsBookTag(1L)).isTrue();
    }

    @Test
    @DisplayName("existsBookTag - 이름으로 존재 여부")
    void existsBookTagByName() {
        when(bookTagRepository.existsBookTagByName("tag1")).thenReturn(true);

        assertThat(bookTagService.existsBookTag("tag1")).isTrue();
    }

}