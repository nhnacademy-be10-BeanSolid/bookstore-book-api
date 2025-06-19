package com.nhnacademy.bookapi.booktag.controller;

import com.nhnacademy.bookapi.advice.ValidationFailedException;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagUpdateRequest;
import com.nhnacademy.bookapi.booktag.service.BookTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/book-tags")
@RequiredArgsConstructor
public class BookTagController {
    private final BookTagService bookTagService;

    @GetMapping
    public ResponseEntity<List<BookTagResponse>> getBookTags() {
        List<BookTagResponse> response = bookTagService.getBookTags();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<BookTagResponse> getBookTag(@PathVariable Long tagId) {
        BookTagResponse response = bookTagService.getBookTag(tagId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BookTagResponse> createBookTag(@Valid @RequestBody BookTagCreateRequest request,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        BookTagResponse response = bookTagService.createBookTag(request);
        URI location = URI.create("/book-tags/" + response.tagId());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{tagId}")
    public ResponseEntity<BookTagResponse> updateBookTag(@PathVariable Long tagId, @Valid @RequestBody BookTagUpdateRequest request,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        BookTagResponse response = bookTagService.updateBookTag(tagId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteBookTag(@PathVariable Long tagId) {
        bookTagService.deleteBookTag(tagId);
        return ResponseEntity.noContent().build();
    }
}
