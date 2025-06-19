package com.nhnacademy.bookapi.booklike.controller;

import com.nhnacademy.bookapi.advice.InvalidUserIdHeaderException;
import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import com.nhnacademy.bookapi.booklike.service.BookLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookLikeController {

    private final BookLikeService bookLikeService;

    // 마이페이지에서 좋아요 확인
    @GetMapping("/users")
    public ResponseEntity<List<BookLikeResponse>> getBookLikes(@RequestHeader("X-USER-ID") String userId) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidUserIdHeaderException();
        }
        List<BookLikeResponse> bookLikes = bookLikeService.getBookLikesByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(bookLikes);
    }

    @GetMapping("/books/{bookId}/bookLikes")
    public ResponseEntity<List<BookLikeResponse>> getBookLikesByBookId(@PathVariable Long bookId) {
        List<BookLikeResponse> bookLikes = bookLikeService.getBookLikesByBookId(bookId);
        return ResponseEntity.status(HttpStatus.OK).body(bookLikes);
    }

    @PostMapping("/books/{bookId}/bookLikes")
    public ResponseEntity<BookLikeResponse> createBookLike(@PathVariable Long bookId,
                                                           @RequestHeader("X-USER-ID") String userId) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidUserIdHeaderException();
        }
        BookLikeResponse response = bookLikeService.createBookLike(bookId, userId);
        URI location = URI.create("/books/" + bookId + "/bookLikes/" + response.bookLikeId());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/books/{bookId}/bookLikes")
    public ResponseEntity<Void> deleteBookLikeByUserIdAndBookId(@RequestHeader("X-USER-ID") String userId,
                                                                @PathVariable Long bookId) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidUserIdHeaderException();
        }
        bookLikeService.deleteBookLikeByUserIdAndBookId(userId, bookId);
        return ResponseEntity.noContent().build();
    }
}
