package com.nhnacademy.bookapi.bookcategory.service.impl;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryNodeResponse;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryAlreadyExistsException;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryNotFoundException;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {
    private final BookCategoryRepository bookCategoryRepository;

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public BookCategoryResponse createCategory(BookCategoryCreateRequest request) {
        if (existsCategory(request.categoryName())) {
            throw new BookCategoryAlreadyExistsException(request.categoryName());
        }
        BookCategory parent = null;
        if (request.parentId() != null) {
            parent = bookCategoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new BookCategoryNotFoundException(request.parentId()));
        }
        BookCategory saved = bookCategoryRepository.save(new BookCategory(request.categoryName(), parent));

        return bookCategoryRepository.findBookCategoryResponseById(saved.getCategoryId())
                .orElseThrow(() -> new BookCategoryNotFoundException(saved.getCategoryId()));
    }

    @Override
    @Transactional(readOnly = true)
    public BookCategoryResponse getCategoryById(Long categoryId) {
        return bookCategoryRepository.findBookCategoryResponseById(categoryId)
                .orElseThrow(() -> new BookCategoryNotFoundException(categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookCategoryResponse> getAllCategories(Pageable pageable) {
        return bookCategoryRepository.findAllBookCategoryResponse(pageable);
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public BookCategoryResponse updateCategory(Long categoryId, BookCategoryUpdateRequest request) {
        BookCategory category = bookCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BookCategoryNotFoundException(categoryId));
        BookCategory parent = null;
        if (request.parentId() != null) {
            parent = bookCategoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new BookCategoryNotFoundException(request.parentId()));
        }
        category.setName(request.categoryName());
        category.setParentCategory(parent);
        category.setUpdatedAt(LocalDateTime.now());

        bookCategoryRepository.save(category);

        return bookCategoryRepository.findBookCategoryResponseById(categoryId)
                .orElseThrow(() -> new BookCategoryNotFoundException(categoryId));
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long categoryId) {
        if(!existsCategory(categoryId)) {
            throw new BookCategoryNotFoundException(categoryId);
        }

        List<BookCategory> children = bookCategoryRepository.findByParentCategory_CategoryId(categoryId);
        for (BookCategory child : children) {
            log.info("Deleting child category: {}", child.getCategoryId());
            deleteCategory(child.getCategoryId());
        }

        bookCategoryRepository.deleteById(categoryId);
    }

    @Override
    public boolean existsCategory(String categoryName) {
        return bookCategoryRepository.existsByName(categoryName);
    }

    @Override
    public boolean existsCategory(Long categoryId) {
        return bookCategoryRepository.existsById(categoryId);
    }

    @Override
    @Cacheable(value = "categories", key = "'categoryTree'")
    public List<BookCategoryNodeResponse> getCategoryTree() {
        return bookCategoryRepository.buildCategoryTree();
    }
}
