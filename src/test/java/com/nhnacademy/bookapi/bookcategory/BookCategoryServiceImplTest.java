package com.nhnacademy.bookapi.bookcategory;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryAlreadyExistsException;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryNotFoundException;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        when(bookCategoryRepository.existsByName("Parent")).thenReturn(false);

        when(bookCategoryRepository.save(parentCategory)).thenReturn(parentCategory);

        BookCategory result = bookCategoryService.createCategory(parentCategory);

        assertEquals("Parent", result.getName());
        verify(bookCategoryRepository).save(parentCategory);
    }

    @Test
    void createCategory_alreadyExists() {
        when(bookCategoryRepository.existsByName("Parent")).thenReturn(true);

        assertThrows(BookCategoryAlreadyExistsException.class,
                () -> bookCategoryService.createCategory(parentCategory)
        );
    }

    @Test
    void getCategoryById_found() {
        when(bookCategoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));

        BookCategory result = bookCategoryService.getCategoryById(1L);

        assertEquals(1L, result.getCategoryId());
        assertEquals("Parent", result.getName());
    }

    @Test
    void getCategoryById_notFound() {
        when(bookCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookCategoryNotFoundException.class, () -> bookCategoryService.getCategoryById(99L));
    }

    @Test
    void getAllCategories() {
        when(bookCategoryRepository.findAll()).thenReturn(Arrays.asList(parentCategory, childCategory));

        var result = bookCategoryService.getAllCategories();

        assertEquals(2, result.size());
        assertTrue(result.contains(parentCategory));
        assertTrue(result.contains(childCategory));
    }

    @Test
    void updateCategory_success() {
        BookCategory update = new BookCategory("Updated", null);
        update.setCreatedAt(parentCategory.getCreatedAt());

        when(bookCategoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(bookCategoryRepository.save(any(BookCategory.class))).thenAnswer(i -> i.getArgument(0));
        BookCategory result = bookCategoryService.updateCategory(1L, update);

        assertEquals("Updated", result.getName());
        assertNotNull(result.getUpdatedAt());
        verify(bookCategoryRepository).save(parentCategory);
    }

    @Test
    void updateCategory_notFound() {
        BookCategory update = new BookCategory("Updated", null);

        when(bookCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookCategoryNotFoundException.class, () -> bookCategoryService.updateCategory(99L, update));
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

        assertThrows(BookCategoryNotFoundException.class, () -> bookCategoryService.deleteCategory(99L));
    }

    @Test
    void existsCategory_byName() {
        when(bookCategoryRepository.existsByName("Parent")).thenReturn(true);

        assertTrue(bookCategoryService.existsCategory("Parent"));
    }

    @Test
    void existsCategory_ById() {
        when(bookCategoryRepository.existsById(1L)).thenReturn(true);

        assertTrue(bookCategoryService.existsCategory(1L));
    }
}