package com.nhnacademy.bookapi.bookcategory.controller;

import com.nhnacademy.bookapi.adpater.service.UserService;
import com.nhnacademy.bookapi.bookcategory.controller.swagger.BookCategoryControllerDocs;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryNodeResponse;
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
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class BookCategoryController implements BookCategoryControllerDocs {

    private final UserService userService;
    private final BookCategoryService bookCategoryService;

    @GetMapping
    public ResponseEntity<Page<BookCategoryResponse>> getAllCategories(@RequestHeader("X-USER-ID") String xUserId,
                                                                       Pageable pageable) {
        userService.getUserAuthorize(xUserId);
        Page<BookCategoryResponse> bookCategoryList = bookCategoryService.getAllCategories(pageable);
        return ResponseEntity.ok(bookCategoryList);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<BookCategoryResponse> getCategoryById(@RequestHeader("X-USER-ID") String xUserId,
                                                                @PathVariable("categoryId") Long categoryId) {
        userService.getUserAuthorize(xUserId);
        BookCategoryResponse response = bookCategoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BookCategoryResponse> createCategory(@RequestHeader("X-USER-ID") String xUserId,
                                                               @Valid @RequestBody BookCategoryCreateRequest request,
                                                               BindingResult bindingResult) {
        userService.getUserAuthorize(xUserId);
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        BookCategoryResponse response = bookCategoryService.createCategory(request);
        URI location = URI.create("/categories/" + response.categoryId());

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<BookCategoryResponse> updateCategory(@RequestHeader("X-USER-ID") String xUserId,
                                                               @PathVariable("categoryId") Long categoryId,
                                                               @Valid @RequestBody BookCategoryUpdateRequest request,
                                                               BindingResult bindingResult) {
        userService.getUserAuthorize(xUserId);
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        BookCategoryResponse response = bookCategoryService.updateCategory(categoryId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@RequestHeader("X-USER-ID") String xUserId,
                                               @PathVariable("categoryId") Long categoryId) {
        userService.getUserAuthorize(xUserId);
        bookCategoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tree")
    public ResponseEntity<List<BookCategoryNodeResponse>> getCategoryTree() {
        List<BookCategoryNodeResponse> response = bookCategoryService.getCategoryTree();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookCategoryResponse>> getAllCategoriesList() {
        List<BookCategoryResponse> response = bookCategoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }
}
