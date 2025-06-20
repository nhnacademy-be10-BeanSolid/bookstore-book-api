package com.nhnacademy.bookapi.bookcategory.service.impl;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryAlreadyExistsException;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryNotFoundException;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {
    private final BookCategoryRepository bookCategoryRepository;

    @Override
    public BookCategoryResponse createCategory(BookCategoryCreateRequest request) {
        if (existsCategory(request.name())) {
            throw new BookCategoryAlreadyExistsException(request.name());
        }
        BookCategory parent = null;
        if (request.parentId() != null) {
            parent = bookCategoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new BookCategoryNotFoundException(request.parentId()));
        }
        BookCategory saved = bookCategoryRepository.save(new BookCategory(request.name(), parent));

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
    public List<BookCategoryResponse> getAllCategories() {
        return bookCategoryRepository.findAllBookCategoryResponse();
    }

    @Override
    public BookCategoryResponse updateCategory(Long categoryId, BookCategoryUpdateRequest request) {
        BookCategory category = bookCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BookCategoryNotFoundException(categoryId));

        BookCategory parent = null;
        if (request.parentId() != null) {
            parent = bookCategoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new BookCategoryNotFoundException(request.parentId()));
        }
        category.setName(request.name());
        category.setParentCategory(parent);
        category.setUpdatedAt(LocalDateTime.now());

        bookCategoryRepository.save(category);

        return bookCategoryRepository.findBookCategoryResponseById(categoryId)
                .orElseThrow(() -> new BookCategoryNotFoundException(categoryId));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        if(!existsCategory(categoryId)) {
            throw new BookCategoryNotFoundException(categoryId);
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
}
