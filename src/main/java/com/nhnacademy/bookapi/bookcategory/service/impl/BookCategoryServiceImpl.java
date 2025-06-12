package com.nhnacademy.bookapi.bookcategory.service.impl;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryAlreadyExistsException;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryNotFoundException;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {
    private final BookCategoryRepository bookCategoryRepository;

    @Override
    public BookCategory createCategory(BookCategory bookCategory) {
        if(existsCategory(bookCategory.getName())) {
            throw new BookCategoryAlreadyExistsException(bookCategory.getName());
        }
        return bookCategoryRepository.save(bookCategory);
    }

    @Override
    public BookCategory getCategoryById(Long categoryId) {
        Optional<BookCategory> bookCategory = bookCategoryRepository.findById(categoryId);
        return bookCategory.orElseThrow(() -> new BookCategoryNotFoundException(categoryId));
    }

    @Override
    public List<BookCategory> getAllCategories() {
        return bookCategoryRepository.findAll();
    }

    @Override
    public BookCategory updateCategory(Long categoryId, BookCategory bookCategory) {
        Optional<BookCategory> bookCategoryOptional = bookCategoryRepository.findById(categoryId);
        if(bookCategoryOptional.isPresent()) {
            BookCategory bookCategoryToUpdate = bookCategoryOptional.get();
            bookCategoryToUpdate.setName(bookCategory.getName());
            bookCategoryToUpdate.setParentCategory(bookCategory.getParentCategory());
            bookCategoryToUpdate.setUpdatedAt(LocalDateTime.now());
            return bookCategoryRepository.save(bookCategoryToUpdate);
        }
        throw new BookCategoryNotFoundException(categoryId);
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
