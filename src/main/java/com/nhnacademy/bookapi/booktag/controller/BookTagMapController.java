package com.nhnacademy.bookapi.booktag.controller;

import com.nhnacademy.bookapi.common.annotation.AuthenticatedUserId;
import com.nhnacademy.bookapi.common.exception.ValidationFailedException;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.nhnacademy.bookapi.booktag.service.BookTagMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/tags")
public class BookTagMapController {

    private final BookTagMapService bookTagMapService;

    @GetMapping
    public ResponseEntity<BookTagMapResponse> getBookTagMapResponse(@PathVariable Long bookId) {
        BookTagMapResponse response = bookTagMapService.getBookTagMapResponse(bookId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BookTagMapResponse> createBookTagMap(@AuthenticatedUserId String userId,
                                                               @PathVariable Long bookId,
                                                               @Valid @RequestBody BookTagMapCreateRequest request,
                                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        BookTagMapResponse response = bookTagMapService.createBookTag(bookId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteBookTagMap(@AuthenticatedUserId String userId,
                                                 @PathVariable Long bookId,
                                                 @PathVariable Long tagId) {
        bookTagMapService.deleteBookTag(bookId, tagId);
        return ResponseEntity.noContent().build();
    }
}
