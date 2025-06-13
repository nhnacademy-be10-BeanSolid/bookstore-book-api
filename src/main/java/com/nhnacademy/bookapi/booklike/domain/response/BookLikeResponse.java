package com.nhnacademy.bookapi.booklike.domain.response;

import com.nhnacademy.bookapi.booklike.domain.BookLike;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookLikeResponse {

    Long bookLikeId;
    LocalDateTime likedAt;
    String userId;
    Long bookId;

    public static BookLikeResponse of(BookLike bookLike) {
        return new BookLikeResponse(bookLike.getId(),
                bookLike.getLikedAt(),
                bookLike.getUserId(),
                bookLike.getBook().getId()
        );
    }
}
