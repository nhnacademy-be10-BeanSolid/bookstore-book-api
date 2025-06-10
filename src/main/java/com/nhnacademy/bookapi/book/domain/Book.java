package com.nhnacademy.bookapi.book.domain;

import com.nhnacademy.bookapi.booktag.domain.BookTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    private LocalDateTime createAt;

    @Column(name = "updated_at")

    private LocalDateTime updateAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;

    @Column(nullable = false)
    private int stock;

    @ManyToMany
    @JoinTable(
            name = "book_tag_map",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<BookTag> bookTags = new HashSet<>();

    public Book(String title, String description, String toc, String publisher, String author, LocalDate publishedDate,
                String isbn, int originalPrice, int salePrice, boolean wrappable, int stock) {
        this.title = title;
        this.description = description;
        this.toc = toc;
        this.publisher = publisher;
        this.author = author;
        this.publishedDate = publishedDate;
        this.isbn = isbn;
        this.originalPrice = originalPrice;
        this.salePrice = salePrice;
        this.wrappable = wrappable;
        this.createAt = LocalDateTime.now();
        this.status = BookStatus.ON_SALE;
        this.stock = stock;
    }
}
