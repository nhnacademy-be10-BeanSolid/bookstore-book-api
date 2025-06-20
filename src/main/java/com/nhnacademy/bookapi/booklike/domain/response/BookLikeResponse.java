package com.nhnacademy.bookapi.booklike.domain.response;

import com.nhnacademy.bookapi.booklike.domain.BookLike;

import java.time.LocalDateTime;

public record BookLikeResponse(
        Long bookLikeId,
        LocalDateTime likedAt,
        String userId,
        Long bookId
) {
    public static BookLikeResponse from(BookLike bookLike) {
        return new BookLikeResponse(
                bookLike.getId(),
                bookLike.getLikedAt(),
                bookLike.getUserId(),
                bookLike.getBook().getId()
        );
    }
}
