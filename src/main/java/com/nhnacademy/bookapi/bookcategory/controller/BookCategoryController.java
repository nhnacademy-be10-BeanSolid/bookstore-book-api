package com.nhnacademy.bookapi.bookcategory.controller;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class BookCategoryController {

    private final BookCategoryService bookCategoryService;

    @GetMapping
    public ResponseEntity<List<BookCategoryResponse>> getAllCategories() {
        List<BookCategory> bookCategoryList = bookCategoryService.getAllCategories();
        List<BookCategoryResponse> bookCategoryResponseList = new ArrayList<>();
        for (BookCategory bookCategory : bookCategoryList) {
            bookCategoryResponseList.add(createBookCategoryResponse(bookCategory));
        }
        return ResponseEntity.ok(bookCategoryResponseList);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<BookCategoryResponse> getCategoryById(@PathVariable("categoryId") Long categoryId) {
        BookCategory bookCategory = bookCategoryService.getCategoryById(categoryId);
        BookCategoryResponse bookCategoryResponse = createBookCategoryResponse(bookCategory);

        return ResponseEntity.ok(bookCategoryResponse);
    }

    @PostMapping
    public ResponseEntity<BookCategoryResponse> createCategory(@Valid @RequestBody BookCategoryCreateRequest request) {
        BookCategory parentCategory = getParentCategory(request.parentId());
        BookCategory bookCategory = new BookCategory(request.name(), parentCategory);

        BookCategory createdBookCategory = bookCategoryService.createCategory(bookCategory);

        BookCategoryResponse bookCategoryResponse = createBookCategoryResponse(createdBookCategory);

        URI location = URI.create("/categories/" + createdBookCategory.getCategoryId());

        return ResponseEntity.created(location).body(bookCategoryResponse);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<BookCategoryResponse> updateCategory(@PathVariable("categoryId") Long categoryId, @Valid @RequestBody BookCategoryUpdateRequest request) {
        BookCategory parentCategory = getParentCategory(request.parentId());
        BookCategory bookCategory = new BookCategory(request.name(), parentCategory);
        BookCategory updatedBookCategory = bookCategoryService.updateCategory(categoryId, bookCategory);
        return ResponseEntity.ok(createBookCategoryResponse(updatedBookCategory));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        bookCategoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    private static BookCategoryResponse createBookCategoryResponse(BookCategory bookCategory) {
        return new BookCategoryResponse(
                bookCategory.getCategoryId(),
                bookCategory.getParentCategory() != null ?
                bookCategory.getParentCategory().getCategoryId() : null,
                bookCategory.getName(),
                bookCategory.getCreatedAt(),
                bookCategory.getUpdatedAt()
        );
    }

    private BookCategory getParentCategory(Long request) {
        BookCategory parentCategory = null;
        if (request != null && bookCategoryService.existsCategory(request)) {
            parentCategory = bookCategoryService.getCategoryById(request);
        }
        return parentCategory;
    }
}
