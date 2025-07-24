package com.nhnacademy.bookapi.bookcategory.controller;

import com.nhnacademy.bookapi.adpater.service.UserService;
import com.nhnacademy.bookapi.bookcategory.controller.swagger.BookCategoryMapControllerDocs;
import com.nhnacademy.bookapi.common.exception.ValidationFailedException;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryMapCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/categories")
public class BookCategoryMapController implements BookCategoryMapControllerDocs {

    private final UserService userService;
    private final BookCategoryMapService bookCategoryMapService;

    @GetMapping
    public ResponseEntity<BookCategoryMapResponse> getBookCategoryMapResponse(@PathVariable Long bookId) {
        BookCategoryMapResponse response = bookCategoryMapService.getBookCategoryMapResponse(bookId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BookCategoryMapResponse> createBookCategoryMap(@RequestHeader("X-USER-ID") String xUserId,
                                                                         @PathVariable Long bookId,
                                                                         @Valid @RequestBody BookCategoryMapCreateRequest request,
                                                                         BindingResult bindingResult) {
        userService.getUserAuthorize(xUserId);
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        BookCategoryMapResponse response = bookCategoryMapService.createBookCategoryMap(bookId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategoryMap(@RequestHeader("X-USER-ID") String xUserId,
                                                  @PathVariable Long bookId,
                                                  @PathVariable Long categoryId) {
        userService.getUserAuthorize(xUserId);
        bookCategoryMapService.deleteCategoryMap(bookId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
