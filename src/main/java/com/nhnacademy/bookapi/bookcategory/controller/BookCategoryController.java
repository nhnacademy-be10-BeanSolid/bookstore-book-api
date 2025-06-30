package com.nhnacademy.bookapi.bookcategory.controller;

import com.nhnacademy.bookapi.advice.ValidationFailedException;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryService;
import com.nhnacademy.bookapi.bookcategory.service.CategoryCsvFileReadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class BookCategoryController {

    private final BookCategoryService bookCategoryService;
    private final CategoryCsvFileReadService categoryCsvFileReadService;

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
    public ResponseEntity<BookCategoryResponse> createCategory(@Valid @RequestBody BookCategoryCreateRequest request,
                                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        BookCategoryResponse response = bookCategoryService.createCategory(request);
        URI location = URI.create("/categories/" + response.categoryId());

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{categoryId}")
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

    // 임시 경로
    @PostMapping("/import-categories")
    public ResponseEntity<String> importCategories(@RequestParam("file") MultipartFile file) throws IOException {
        File convFile = File.createTempFile("tmp", ".csv");
        file.transferTo(convFile);

        categoryCsvFileReadService.importCategoriesFromCsv(convFile);

        return ResponseEntity.ok("Import completed");
    }

}
