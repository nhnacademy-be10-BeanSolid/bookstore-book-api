package com.nhnacademy.bookapi.book.domain;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.booklike.domain.BookLike;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@Entity
@Table(name = "book")
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String toc;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(name = "pulisher_at", nullable = false)
    private LocalDate publishedDate;

    @Column(nullable = false)
    private String isbn;

    @Column(name = "price_original", nullable = false)
    private int originalPrice;

    @Column(name = "price_sale", nullable = false)
    private int salePrice;

    @Column(name = "is_gift_wrappable", nullable = false)
    private boolean wrappable;

    @Column(name = "create_at", nullable = false)
    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookStatus status = BookStatus.ON_SALE;

    @Column(nullable = false)
    private int stock;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "book_tag_map",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<BookTag> bookTags = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "book_category_map",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<BookCategory> bookCategories = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookLike> bookLikes = new HashSet<>();
}
