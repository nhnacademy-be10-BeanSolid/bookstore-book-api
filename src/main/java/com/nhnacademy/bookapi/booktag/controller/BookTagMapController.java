package com.nhnacademy.bookapi.booktag.controller;

import com.nhnacademy.bookapi.adpater.service.UserService;
import com.nhnacademy.bookapi.booktag.controller.swagger.BookTagMapControllerDocs;
import com.nhnacademy.bookapi.common.exception.ValidationFailedException;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.nhnacademy.bookapi.booktag.service.BookTagMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/tags")
public class BookTagMapController implements BookTagMapControllerDocs {

    private final BookTagMapService bookTagMapService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<BookTagMapResponse> getBookTagMapResponse(@PathVariable Long bookId) {
        BookTagMapResponse response = bookTagMapService.getBookTagMapResponse(bookId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BookTagMapResponse> createBookTagMap(@RequestHeader("X-USER-ID") String xUserId,
                                                               @PathVariable Long bookId,
                                                               @Valid @RequestBody BookTagMapCreateRequest request,
                                                               BindingResult bindingResult) {
        userService.getUserAuthorize(xUserId);
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        BookTagMapResponse response = bookTagMapService.createBookTag(bookId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteBookTagMap(@RequestHeader("X-USER-ID") String xUserId,
                                                 @PathVariable Long bookId,
                                                 @PathVariable Long tagId) {
        userService.getUserAuthorize(xUserId);
        bookTagMapService.deleteBookTag(bookId, tagId);
        return ResponseEntity.noContent().build();
    }
}
