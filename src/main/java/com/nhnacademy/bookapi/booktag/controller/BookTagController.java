package com.nhnacademy.bookapi.booktag.controller;

import com.nhnacademy.bookapi.common.annotation.AuthenticatedUserId;
import com.nhnacademy.bookapi.common.exception.ValidationFailedException;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagUpdateRequest;
import com.nhnacademy.bookapi.booktag.service.BookTagService;
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
@RequestMapping("/book-tags")
@RequiredArgsConstructor
public class BookTagController {
    private final BookTagService bookTagService;

    @GetMapping
    public ResponseEntity<Page<BookTagResponse>> getBookTags(@AuthenticatedUserId String userId,
                                                             Pageable pageable) {
        Page<BookTagResponse> response = bookTagService.getBookTags(pageable);
        log.info("userId = {}", userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<BookTagResponse> getBookTag(@AuthenticatedUserId String userId,
                                                      @PathVariable Long tagId) {
        BookTagResponse response = bookTagService.getBookTag(tagId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BookTagResponse> createBookTag(@AuthenticatedUserId String userId,
                                                         @Valid @RequestBody BookTagCreateRequest request,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        BookTagResponse response = bookTagService.createBookTag(request);
        URI location = URI.create("/book-tags/" + response.tagId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<BookTagResponse> updateBookTag(@AuthenticatedUserId String userId,
                                                         @PathVariable Long tagId,
                                                         @Valid @RequestBody BookTagUpdateRequest request,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        BookTagResponse response = bookTagService.updateBookTag(tagId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteBookTag(@AuthenticatedUserId String userId,
                                              @PathVariable Long tagId) {
        bookTagService.deleteBookTag(tagId);
        return ResponseEntity.noContent().build();
    }
}
