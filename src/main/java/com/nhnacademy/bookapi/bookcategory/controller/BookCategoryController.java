package com.nhnacademy.bookapi.bookcategory.controller;

import com.nhnacademy.bookapi.advice.ValidationFailedException;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
        List<BookCategoryResponse> bookCategoryList = bookCategoryService.getAllCategories();
        return ResponseEntity.ok(bookCategoryList);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<BookCategoryResponse> getCategoryById(@PathVariable("categoryId") Long categoryId) {
        BookCategoryResponse response = bookCategoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BookCategoryResponse> createCategory(@Valid @RequestBody BookCategoryCreateRequest request,
                                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        BookCategoryResponse response = bookCategoryService.createCategory(request);
        URI location = URI.create("/categories/" + response.categoryId());

        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<BookCategoryResponse> updateCategory(@PathVariable("categoryId") Long categoryId,
                                                               @Valid @RequestBody BookCategoryUpdateRequest request,
                                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        BookCategoryResponse response = bookCategoryService.updateCategory(categoryId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        bookCategoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
