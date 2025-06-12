package com.nhnacademy.bookapi.booktag.controller;

import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.controller.request.BookTagCreateRequest;
import com.nhnacademy.bookapi.booktag.controller.response.BookTagResponse;
import com.nhnacademy.bookapi.booktag.controller.request.BookTagUpdateRequest;
import com.nhnacademy.bookapi.booktag.service.BookTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/book-tags")
@RequiredArgsConstructor
public class BookTagController {
    private final BookTagService bookTagService;

    @GetMapping
    public ResponseEntity<List<BookTagResponse>> getBookTags() {
        List<BookTag> bookTagList = bookTagService.getBookTags();
        List<BookTagResponse> bookTagResponseList = new ArrayList<>();
        bookTagList.forEach(bookTag -> {
            bookTagResponseList.add(new BookTagResponse(bookTag.getTagId(), bookTag.getName()));
        });
        return ResponseEntity.ok(bookTagResponseList);
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<BookTagResponse> getBookTag(@PathVariable Long tagId) {
        BookTag bookTag = bookTagService.getBookTag(tagId);
        BookTagResponse bookTagResponse = new BookTagResponse(bookTag.getTagId(), bookTag.getName());
        return ResponseEntity.ok(bookTagResponse);
    }

    @PostMapping
    public ResponseEntity<BookTagResponse> createBookTag(@Valid @RequestBody BookTagCreateRequest request) {
        BookTag bookTag = new BookTag(request.name());
        BookTag savedBookTag = bookTagService.createBookTag(bookTag);
        BookTagResponse bookTagResponse = new BookTagResponse(savedBookTag.getTagId(), savedBookTag.getName());
        URI location = URI.create("/book-tags/" + savedBookTag.getTagId());
        return ResponseEntity.created(location).body(bookTagResponse);
    }

    @PatchMapping("/{tagId}")
    public ResponseEntity<BookTagResponse> updateBookTag(@PathVariable Long tagId, @Valid @RequestBody BookTagUpdateRequest request) {
        BookTag bookTag = new BookTag(request.name());
        bookTag.setTagId(tagId);
        BookTag updatedBookTag = bookTagService.updateBookTag(bookTag);
        BookTagResponse bookTagResponse = new BookTagResponse(updatedBookTag.getTagId(), updatedBookTag.getName());
        return ResponseEntity.ok(bookTagResponse);
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteBookTag(@PathVariable Long tagId) {
        bookTagService.deleteBookTag(tagId);
        return ResponseEntity.noContent().build();
    }
}
