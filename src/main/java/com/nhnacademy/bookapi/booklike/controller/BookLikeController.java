package com.nhnacademy.bookapi.booklike.controller;

import com.nhnacademy.bookapi.common.exception.InvalidHeaderException;
import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import com.nhnacademy.bookapi.booklike.service.BookLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class BookLikeController {

    private final BookLikeService bookLikeService;

    // 마이페이지에서 좋아요 확인
    @GetMapping("/users")
    public ResponseEntity<Page<BookLikeResponse>> getBookLikes(@RequestHeader("X-USER-ID") String userId, Pageable pageable) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidHeaderException();
        }
        Page<BookLikeResponse> bookLikes = bookLikeService.getBookLikesByUserId(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(bookLikes);
    }

    // 도서상세에서 확인
    @GetMapping("/books/{bookId}/bookLikes")
    public ResponseEntity<Page<BookLikeResponse>> getBookLikesByBookId(@PathVariable Long bookId, Pageable pageable) {
        Page<BookLikeResponse> bookLikes = bookLikeService.getBookLikesByBookId(bookId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(bookLikes);
    }

    @PostMapping("/books/{bookId}/bookLikes")
    public ResponseEntity<BookLikeResponse> createBookLike(@PathVariable Long bookId,
                                                           @RequestHeader("X-USER-ID") String userId) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidHeaderException();
        }
        BookLikeResponse response = bookLikeService.createBookLike(bookId, userId);
        URI location = URI.create("/books/" + bookId + "/bookLikes/" + response.bookLikeId());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/books/{bookId}/bookLikes")
    public ResponseEntity<Void> deleteBookLikeByUserIdAndBookId(@RequestHeader("X-USER-ID") String userId,
                                                                @PathVariable Long bookId) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidHeaderException();
        }
        bookLikeService.deleteBookLikeByUserIdAndBookId(userId, bookId);
        return ResponseEntity.noContent().build();
    }
}
