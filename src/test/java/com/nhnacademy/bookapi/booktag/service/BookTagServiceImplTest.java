package com.nhnacademy.bookapi.booktag.service;

import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagUpdateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookTagServiceImplTest {

    @Mock
    BookTagRepository bookTagRepository;

    @InjectMocks
    BookTagServiceImpl bookTagService;

    @Test
    @DisplayName("전체 조회")
    void getBookTags() {
        List<BookTagResponse> response = List.of(
                new BookTagResponse(1L, "tag1"),
                new BookTagResponse(2L, "tag2")
        );

        when(bookTagRepository.findAllBookTagResponses()).thenReturn(response);

        List<BookTagResponse> tags = bookTagService.getBookTags();

        assertThat(tags).hasSize(2);
        verify(bookTagRepository).findAllBookTagResponses();
    }

    @Test
    @DisplayName("태그 조회")
    void getBookTag_found() {
        BookTagResponse response = new BookTagResponse(1L, "tag1");
        when(bookTagRepository.findBookTagResponseById(1L)).thenReturn(Optional.of(response));

        BookTagResponse result = bookTagService.getBookTag(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("태그 생성 - 이미 존재하는 태그")
    void createBookTag_duplicateName() {
        when(bookTagRepository.existsBookTagByName("tag1")).thenReturn(true);

        BookTagCreateRequest request = new BookTagCreateRequest("tag1");
        assertThatThrownBy(() -> bookTagService.createBookTag(request))
                .isInstanceOf(BookTagAlreadyExistsException.class);
    }

    @Test
    @DisplayName("태그 생성")
    void createBookTag_success() {
        BookTagCreateRequest request = new BookTagCreateRequest("tag1");
        when(bookTagRepository.existsBookTagByName("tag1")).thenReturn(false);

        BookTag saved = new BookTag(1L,"tag1");

        when(bookTagRepository.save(any(BookTag.class))).thenReturn(saved);
        when(bookTagRepository.findBookTagResponseById(1L)).thenReturn(Optional.of(new BookTagResponse(1L, "tag1")));

        BookTagResponse response = bookTagService.createBookTag(request);

        assertThat(response.name()).isEqualTo("tag1");
        verify(bookTagRepository).save(any(BookTag.class));
    }

    @Test
    @DisplayName("업데이트 - 중복 이름")
    void updateBookTag_duplicateName() {
        BookTag tag = new BookTag(1L, "tag");
        BookTagUpdateRequest request = new BookTagUpdateRequest("tag1");

        when(bookTagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(bookTagRepository.existsBookTagByName("tag1")).thenReturn(true);

        assertThatThrownBy(() -> bookTagService.updateBookTag(1L, request))
                .isInstanceOf(BookTagAlreadyExistsException.class);
    }

    @Test
    @DisplayName("업데이트")
    void updateBookTag_success() {
        BookTag tag = new BookTag(1L, "tag");
        BookTagUpdateRequest request = new BookTagUpdateRequest("tag1");
        BookTagResponse response = new BookTagResponse(1L, "tag1");

        when(bookTagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(bookTagRepository.existsBookTagByName("tag1")).thenReturn(false);
        when(bookTagRepository.findBookTagResponseById(1L)).thenReturn(Optional.of(response));

        BookTagResponse updatedResponse = bookTagService.updateBookTag(1L, request);

        assertThat(updatedResponse.name()).isEqualTo("tag1");
    }

    @Test
    @DisplayName("삭제 - 존재하지 않는 태그")
    void deleteBookTag_notFound() {
        when(bookTagRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> bookTagService.deleteBookTag(1L))
                .isInstanceOf(BookTagNotFoundException.class);
    }

    @Test
    @DisplayName("삭제")
    void deleteBookTag_success() {
        when(bookTagRepository.existsById(1L)).thenReturn(true);

        bookTagService.deleteBookTag(1L);

        verify(bookTagRepository).deleteById(1L);
    }

    @Test
    @DisplayName("id로 존재 여부")
    void existsBookTagById() {
        when(bookTagRepository.existsById(1L)).thenReturn(true);

        assertThat(bookTagService.existsBookTag(1L)).isTrue();
    }

    @Test
    @DisplayName("이름으로 존재 여부")
    void existsBookTagByName() {
        when(bookTagRepository.existsBookTagByName("tag1")).thenReturn(true);

        assertThat(bookTagService.existsBookTag("tag1")).isTrue();
    }

}