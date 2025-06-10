package com.nhnacademy.bookapi.bookLike.controller;

import com.nhnacademy.bookapi.bookLike.controller.request.BookLikeCreateRequest;
import com.nhnacademy.bookapi.bookLike.controller.response.BookLikeResponse;
import com.nhnacademy.bookapi.bookLike.service.BookLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookLikeController {

    private final BookLikeService bookLikeService;

    // 좋아요 누른 도서는 마이페이지에서 확인가능 > 경로를 어떻게할지
    @GetMapping("/api/books/{bookId}/bookLikes")
    public ResponseEntity<List<BookLikeResponse>> getBookLikesByBookId(@PathVariable Long bookId) {
        List<BookLikeResponse> bookLikes = bookLikeService.getBookLikesByBookId(bookId);
        return ResponseEntity.status(HttpStatus.OK).body(bookLikes);
    }

    @PostMapping("/api/books/{bookId}/bookLikes")
    public ResponseEntity<BookLikeResponse> createBookLike(@RequestBody BookLikeCreateRequest request) {
        BookLikeResponse response = bookLikeService.createBookLike(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 유저 아이디와 도서 아이디를 어떻게 받을지    POST 요청처럼?
    @DeleteMapping("/api/")
    public ResponseEntity<Void> deleteBookLikeByUserIdAndBookId(String userId, Long bookId) {
        bookLikeService.deleteBookLikeByUserIdAndBookId(userId, bookId);
        return ResponseEntity.noContent().build();
    }

    // 도서 아이디로 해당 도서에 대한 좋아요 전체 삭제
    @DeleteMapping("/api/books/{bookId}/bookLikes")
    public ResponseEntity<Void> deleteBookLikesByBookId(@PathVariable Long bookId) {
        bookLikeService.deleteBookLikeByBookId(bookId);
        return ResponseEntity.noContent().build();
    }
}
