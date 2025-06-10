package com.nhnacademy.bookapi.bookLike.domain;

import com.nhnacademy.bookapi.book.domain.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "book_like")
@NoArgsConstructor
@AllArgsConstructor
public class BookLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookLike_id")
    private Long id;

    @Column(name = "liked_at", nullable = false)
    private LocalDateTime likedAt;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    public BookLike(String userId, Book book) {
        this.userId = userId;
        this.book = book;
        this.likedAt = LocalDateTime.now();
    }
}
