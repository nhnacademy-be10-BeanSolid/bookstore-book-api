package com.nhnacademy.bookapi.bookcategory.service;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryAlreadyExistsException;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryNotFoundException;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.bookcategory.service.impl.BookCategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCategoryServiceImplTest {

    @Mock
    BookCategoryRepository bookCategoryRepository;

    @InjectMocks
    BookCategoryServiceImpl bookCategoryService;

    BookCategory parentCategory;
    BookCategory childCategory;

    @BeforeEach
    void setUp() {
        parentCategory = new BookCategory("Parent", null);
        parentCategory.setCategoryId(1L);
        parentCategory.setCreatedAt(LocalDateTime.now());

        childCategory = new BookCategory("Child", parentCategory);
        childCategory.setCategoryId(2L);
        childCategory.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createCategory_success() {
        BookCategoryCreateRequest request = new BookCategoryCreateRequest("Parent", null);

        Long parentId = parentCategory.getParentCategory() != null ? parentCategory.getParentCategory().getCategoryId() : null;

        BookCategoryResponse response = new BookCategoryResponse(1L,
                parentId,
                "Parent",
                parentCategory.getCreatedAt(),
                parentCategory.getUpdatedAt());

        when(bookCategoryRepository.existsByName("Parent")).thenReturn(false);
        when(bookCategoryRepository.save(any(BookCategory.class))).thenReturn(parentCategory);
        when(bookCategoryRepository.findBookCategoryResponseById(1L)).thenReturn(Optional.of(response));

        BookCategoryResponse result = bookCategoryService.createCategory(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void createCategory_alreadyExists() {
        when(bookCategoryRepository.existsByName("Parent")).thenReturn(true);

        BookCategoryCreateRequest request = new BookCategoryCreateRequest("Parent", null);

        assertThatThrownBy(() -> bookCategoryService.createCategory(request))
                .isInstanceOf(BookCategoryAlreadyExistsException.class);
    }

    @Test
    void getCategoryById() {
        Long parentId = parentCategory.getParentCategory() != null ? parentCategory.getParentCategory().getCategoryId() : null;

        BookCategoryResponse bookCategoryResponse = new BookCategoryResponse(parentCategory.getCategoryId(),
                parentId,
                parentCategory.getName(),
                parentCategory.getCreatedAt(),
                parentCategory.getUpdatedAt());
        when(bookCategoryRepository.findBookCategoryResponseById(1L)).thenReturn(Optional.of(bookCategoryResponse));

        BookCategoryResponse result = bookCategoryService.getCategoryById(1L);

        assertThat(result.categoryId()).isEqualTo(1L);
        assertThat(result.categoryName()).isEqualTo("Parent");
    }

    @Test
    void getCategoryById_notFound() {
        when(bookCategoryRepository.findBookCategoryResponseById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCategoryService.getCategoryById(99L))
                .isInstanceOf(BookCategoryNotFoundException.class);
    }

    @Test
    void getAllCategories() {
        BookCategoryResponse parentResponse = new BookCategoryResponse(
                parentCategory.getCategoryId(),
                null,
                parentCategory.getName(),
                parentCategory.getCreatedAt(),
                parentCategory.getUpdatedAt()
        );

        BookCategoryResponse childResponse = new BookCategoryResponse(
                childCategory.getCategoryId(),
                childCategory.getParentCategory().getCategoryId(),
                childCategory.getName(),
                childCategory.getCreatedAt(),
                childCategory.getUpdatedAt()
        );

        when(bookCategoryRepository.findAllBookCategoryResponse()).thenReturn(List.of(parentResponse, childResponse));

        List<BookCategoryResponse> result = bookCategoryService.getAllCategories();

        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(parentResponse, childResponse);
    }

    @Test
    void updateCategory_success() {
        BookCategoryUpdateRequest request = new BookCategoryUpdateRequest("Updated", null);
        BookCategoryResponse response = new BookCategoryResponse(parentCategory.getCategoryId(), null, "Updated",
                parentCategory.getCreatedAt(), LocalDateTime.now());

        when(bookCategoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(bookCategoryRepository.findBookCategoryResponseById(1L)).thenReturn(Optional.of(response));
        BookCategoryResponse result = bookCategoryService.updateCategory(1L, request);

        assertThat(result.categoryName()).isEqualTo("Updated");
        assertThat(result.updatedAt()).isNotNull();
        verify(bookCategoryRepository).save(parentCategory);
    }

    @Test
    void updateCategory_notFound() {
        BookCategoryUpdateRequest request = new BookCategoryUpdateRequest("Updated", 99L);

        when(bookCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCategoryService.updateCategory(99L, request))
                .isInstanceOf(BookCategoryNotFoundException.class);
    }

    @Test
    void deleteCategory_success() {
        when(bookCategoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookCategoryRepository).deleteById(1L);

        bookCategoryService.deleteCategory(1L);

        verify(bookCategoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_notFound() {
        when(bookCategoryRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> bookCategoryService.deleteCategory(99L))
                .isInstanceOf(BookCategoryNotFoundException.class);
    }

    @Test
    void existsCategory_byName() {
        when(bookCategoryRepository.existsByName("Parent")).thenReturn(true);

        assertThat(bookCategoryService.existsCategory("Parent")).isTrue();
    }

    @Test
    void existsCategory_ById() {
        when(bookCategoryRepository.existsById(1L)).thenReturn(true);

        assertThat(bookCategoryService.existsCategory(1L)).isTrue();
    }
}