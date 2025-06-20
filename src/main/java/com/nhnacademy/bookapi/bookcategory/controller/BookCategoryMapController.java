package com.nhnacademy.bookapi.bookcategory.controller;

import com.nhnacademy.bookapi.advice.ValidationFailedException;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryMapCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("books/{bookId}/categories")
public class BookCategoryMapController {

    private final BookCategoryMapService bookCategoryMapService;

    @PostMapping
    public ResponseEntity<BookCategoryMapResponse> createBookCategoryMap(@PathVariable Long bookId,
                                                                         @Valid @RequestBody BookCategoryMapCreateRequest request,
                                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        BookCategoryMapResponse response = bookCategoryMapService.createBookCategoryMap(bookId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategoryMap(@PathVariable Long bookId, @PathVariable Long categoryId) {
        bookCategoryMapService.deleteCategoryMap(bookId, categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
