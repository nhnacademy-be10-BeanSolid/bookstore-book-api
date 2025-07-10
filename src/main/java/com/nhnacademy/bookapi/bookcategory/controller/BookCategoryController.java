package com.nhnacademy.bookapi.bookcategory.controller;

import com.nhnacademy.bookapi.common.annotation.AuthenticatedUserId;
import com.nhnacademy.bookapi.common.exception.ValidationFailedException;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class BookCategoryController {

    private final BookCategoryService bookCategoryService;

    @GetMapping
    public ResponseEntity<Page<BookCategoryResponse>> getAllCategories(Pageable pageable) {
        Page<BookCategoryResponse> bookCategoryList = bookCategoryService.getAllCategories(pageable);
        return ResponseEntity.ok(bookCategoryList);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<BookCategoryResponse> getCategoryById(@PathVariable("categoryId") Long categoryId) {
        BookCategoryResponse response = bookCategoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BookCategoryResponse> createCategory(@AuthenticatedUserId String userId,
                                                               @Valid @RequestBody BookCategoryCreateRequest request,
                                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        BookCategoryResponse response = bookCategoryService.createCategory(request);
        URI location = URI.create("/categories/" + response.categoryId());

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<BookCategoryResponse> updateCategory(@AuthenticatedUserId String userId,
                                                               @PathVariable("categoryId") Long categoryId,
                                                               @Valid @RequestBody BookCategoryUpdateRequest request,
                                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        BookCategoryResponse response = bookCategoryService.updateCategory(categoryId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@AuthenticatedUserId String userId,
                                               @PathVariable("categoryId") Long categoryId) {
        bookCategoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
