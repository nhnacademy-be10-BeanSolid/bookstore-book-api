package com.nhnacademy.bookapi.bookcategory.service;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryNodeResponse;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryAlreadyExistsException;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryNotFoundException;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.bookcategory.service.impl.BookCategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCategoryServiceTest {

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
    @DisplayName("최상위 카테고리 생성")
    void createCategory_success() {
        BookCategoryCreateRequest request = new BookCategoryCreateRequest("Parent", null);

        Long parentId = parentCategory.getParentCategory() != null ? parentCategory.getParentCategory().getCategoryId() : 1L;

        String parentName = parentCategory.getParentCategory() != null
                ? parentCategory.getParentCategory().getName()
                : null;

        BookCategoryResponse response = new BookCategoryResponse(1L,
                "Parent",
                parentId,
                parentName,
                parentCategory.getCreatedAt(),
                parentCategory.getUpdatedAt());

        when(bookCategoryRepository.existsByName("Parent")).thenReturn(false);
        when(bookCategoryRepository.save(any(BookCategory.class))).thenReturn(parentCategory);
        when(bookCategoryRepository.findBookCategoryResponseById(1L)).thenReturn(Optional.of(response));

        BookCategoryResponse result = bookCategoryService.createCategory(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("카테고리 생성 - 이미 존재하는 카테고리")
    void createCategory_alreadyExists() {
        when(bookCategoryRepository.existsByName("Parent")).thenReturn(true);

        BookCategoryCreateRequest request = new BookCategoryCreateRequest("Parent", null);

        assertThatThrownBy(() -> bookCategoryService.createCategory(request))
                .isInstanceOf(BookCategoryAlreadyExistsException.class);
    }

    @Test
    @DisplayName("하위 카테고리 생성")
    void createCategory_success_withParentId() {
        BookCategoryCreateRequest request = new BookCategoryCreateRequest("Child", 1L);

        BookCategoryResponse response = new BookCategoryResponse(2L,
                "Child", 1L, "Parent", childCategory.getCreatedAt(), null);

        when(bookCategoryRepository.existsByName("Child")).thenReturn(false);
        when(bookCategoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(bookCategoryRepository.save(any(BookCategory.class))).thenReturn(childCategory);
        when(bookCategoryRepository.findBookCategoryResponseById(2L)).thenReturn(Optional.of(response));

        BookCategoryResponse result = bookCategoryService.createCategory(request);

        assertThat(result.categoryId()).isEqualTo(2L);
        assertThat(result.parentId()).isEqualTo(1L);
        assertThat(result.parentCategoryName()).isEqualTo("Parent");
    }

    @Test
    @DisplayName("카테고리 생성 - 존재하지 않는 부모 카테고리")
    void createCategory_InvalidParentId() {
        BookCategoryCreateRequest request = new BookCategoryCreateRequest("Child", 999L);

        when(bookCategoryRepository.existsByName("Child")).thenReturn(false);
        when(bookCategoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCategoryService.createCategory(request))
                .isInstanceOf(BookCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("전체 조회")
    void getAllCategories() {
        Pageable pageable = PageRequest.of(0, 10);
        BookCategoryResponse parentResponse = new BookCategoryResponse(
                parentCategory.getCategoryId(),
                parentCategory.getName(),
                null,
                null,
                parentCategory.getCreatedAt(),
                parentCategory.getUpdatedAt()
        );

        BookCategoryResponse childResponse = new BookCategoryResponse(
                childCategory.getCategoryId(),
                childCategory.getName(),
                childCategory.getParentCategory().getCategoryId(),
                childCategory.getParentCategory().getName(),
                childCategory.getCreatedAt(),
                childCategory.getUpdatedAt()
        );
        Page<BookCategoryResponse> result = new PageImpl<>(List.of(parentResponse, childResponse), pageable, 2);

        when(bookCategoryRepository.findAllBookCategoryResponse(pageable)).thenReturn(result);

        Page<BookCategoryResponse> categories = bookCategoryService.getAllCategories(pageable);

        assertThat(categories.getContent())
                .hasSize(2)
                .extracting(BookCategoryResponse::categoryName)
                .containsExactlyInAnyOrder("Parent", "Child");
    }

    @Test
    @DisplayName("카테고리 조회")
    void getCategoryById() {
        Long parentId = parentCategory.getParentCategory() != null ? parentCategory.getParentCategory().getCategoryId() : 1L;

        String parentName = parentCategory.getParentCategory() != null ? parentCategory.getParentCategory().getName() : null;

        BookCategoryResponse bookCategoryResponse = new BookCategoryResponse(parentCategory.getCategoryId(),
                parentCategory.getName(),
                parentId,
                parentName,
                parentCategory.getCreatedAt(),
                parentCategory.getUpdatedAt());
        when(bookCategoryRepository.findBookCategoryResponseById(1L)).thenReturn(Optional.of(bookCategoryResponse));

        BookCategoryResponse result = bookCategoryService.getCategoryById(1L);

        assertThat(result.categoryId()).isEqualTo(1L);
        assertThat(result.categoryName()).isEqualTo("Parent");
        assertThat(result.createdAt()).isEqualTo(parentCategory.getCreatedAt());
    }

    @Test
    @DisplayName("카테고리 조회 - 존재하지 않는 카테고리")
    void getCategoryById_notFound() {
        when(bookCategoryRepository.findBookCategoryResponseById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCategoryService.getCategoryById(99L))
                .isInstanceOf(BookCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("업데이트")
    void updateCategory_success() {
        BookCategoryUpdateRequest request = new BookCategoryUpdateRequest("Updated", null);
        BookCategoryResponse response = new BookCategoryResponse(parentCategory.getCategoryId(), "Updated", null, null,
                parentCategory.getCreatedAt(), LocalDateTime.now());

        when(bookCategoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(bookCategoryRepository.findBookCategoryResponseById(1L)).thenReturn(Optional.of(response));
        BookCategoryResponse result = bookCategoryService.updateCategory(1L, request);

        assertThat(result.categoryName()).isEqualTo("Updated");
        assertThat(result.updatedAt()).isNotNull();
    }

    @Test
    @DisplayName("업데이트 - 존재하는 부모 카테고리")
    void updateCategory_existsParent() {
        BookCategory category = new BookCategory("test", null);
        ReflectionTestUtils.setField(category, "categoryId", 3L);

        BookCategoryUpdateRequest request = new BookCategoryUpdateRequest("Updated", parentCategory.getCategoryId()); // 아이디 1
        BookCategoryResponse response = new BookCategoryResponse(3L, "Updated", parentCategory.getCategoryId(), parentCategory.getName(),
                category.getCreatedAt(), LocalDateTime.now());

        when(bookCategoryRepository.findById(3L)).thenReturn(Optional.of(category));
        when(bookCategoryRepository.findById(parentCategory.getCategoryId())).thenReturn(Optional.of(parentCategory));
        when(bookCategoryRepository.findBookCategoryResponseById(3L)).thenReturn(Optional.of(response));
        BookCategoryResponse result = bookCategoryService.updateCategory(3L, request);

        assertThat(result.categoryId()).isEqualTo(3L);
        assertThat(result.categoryName()).isEqualTo("Updated");
        assertThat(result.updatedAt()).isNotNull();
    }

    @Test
    @DisplayName("업데이트 - 존재하지 않는 카테고리")
    void updateCategory_notFound() {
        BookCategoryUpdateRequest request = new BookCategoryUpdateRequest("Updated", null);

        when(bookCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCategoryService.updateCategory(99L, request))
                .isInstanceOf(BookCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("업데이트 - 존재하지 않는 부모 카테고리")
    void updateCategory_parentNotFound() {
        BookCategoryUpdateRequest request = new BookCategoryUpdateRequest("Parent", 99L);

        when(bookCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookCategoryService.updateCategory(99L, request))
                .isInstanceOf(BookCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("삭제")
    void deleteCategory_success() {
        when(bookCategoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookCategoryRepository).deleteById(1L);

        bookCategoryService.deleteCategory(1L);

        verify(bookCategoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("삭제 - 존재하지 않는 카테고리")
    void deleteCategory_notFound() {
        when(bookCategoryRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> bookCategoryService.deleteCategory(99L))
                .isInstanceOf(BookCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("이름으로 존재 여부")
    void existsCategory_byName() {
        when(bookCategoryRepository.existsByName("Parent")).thenReturn(true);

        assertThat(bookCategoryService.existsCategory("Parent")).isTrue();
    }

    @Test
    @DisplayName("아이디로 존재 여부")
    void existsCategory_ById() {
        when(bookCategoryRepository.existsById(1L)).thenReturn(true);

        assertThat(bookCategoryService.existsCategory(1L)).isTrue();
    }

    @Test
    @DisplayName("카테고리 트리")
    void getCategoryTree_success() {
        BookCategoryNodeResponse child1 = new BookCategoryNodeResponse(2L, "추리소설", new ArrayList<>());
        BookCategoryNodeResponse child2 = new BookCategoryNodeResponse(3L, "공포소설", new ArrayList<>());
        BookCategoryNodeResponse root = new BookCategoryNodeResponse(1L, "소설", List.of(child1, child2));

        BookCategoryNodeResponse root1 = new BookCategoryNodeResponse(4L, "만화", new ArrayList<>());

        given(bookCategoryRepository.buildCategoryTree()).willReturn(List.of(root, root1));

        List<BookCategoryNodeResponse> actual = bookCategoryService.getCategoryTree();

        assertThat(actual).hasSize(2);
        assertThat(actual).extracting(BookCategoryNodeResponse::categoryId).containsExactly(1L, 4L);
        assertThat(actual.getFirst().children()).hasSize(2);
        assertThat(actual.getFirst().children()).extracting(BookCategoryNodeResponse::categoryName)
                .containsExactlyInAnyOrder("추리소설", "공포소설");

    }
}