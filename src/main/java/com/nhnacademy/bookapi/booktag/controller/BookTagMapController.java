package com.nhnacademy.bookapi.booktag.controller;

import com.nhnacademy.bookapi.advice.ValidationFailedException;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.nhnacademy.bookapi.booktag.service.BookTagMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/tags")
public class BookTagMapController {

    private final BookTagMapService bookTagMapService;

    @PostMapping
    public ResponseEntity<BookTagMapResponse> createBookTagMap(@PathVariable Long bookId,
                                                               @Valid @RequestBody BookTagMapCreateRequest request,
                                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        BookTagMapResponse response = bookTagMapService.createBookTag(bookId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteBookTagMap(@PathVariable Long bookId, @PathVariable Long tagId) {
        bookTagMapService.deleteBookTag(bookId, tagId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
