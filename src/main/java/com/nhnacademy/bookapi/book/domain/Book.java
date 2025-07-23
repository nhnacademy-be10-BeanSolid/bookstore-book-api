package com.nhnacademy.bookapi.book.domain;

import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(columnNames = "isbn")
})
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String toc;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(name = "publish_at", nullable = false)
    private LocalDate publishAt;

    @Column(nullable = false, unique = true)
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

    @Column(columnDefinition = "TEXT")
    private String image;

    @ManyToMany
    @JoinTable(
            name = "book_tag_map",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<BookTag> bookTags = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "book_category_map",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<BookCategory> bookCategories = new HashSet<>();

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    public static Book from(BookCreateRequest request, Set<BookCategory> categories) {
        Book book = new Book();
        book.title = request.title();
        book.description = request.description();
        book.toc = request.toc();
        book.publisher = request.publisher();
        book.author = request.author();
        book.publishAt = request.publishAt();
        book.isbn = request.isbn();
        book.originalPrice = request.originalPrice();
        book.salePrice = request.salePrice();
        book.wrappable = request.wrappable();
        book.stock = request.stock();
        book.image = request.image();
        book.status = BookStatus.ON_SALE;
        book.bookCategories = categories;
        book.viewCount = 0L;
        return book;
    }

    public void updateFrom(BookUpdateRequest request) {
        this.title = request.title();
        this.description = request.description();
        this.toc = request.toc();
        this.publisher = request.publisher();
        this.author = request.author();
        this.publishAt = request.publishAt();
        this.originalPrice = request.originalPrice();
        this.salePrice = request.salePrice();
        this.wrappable = request.wrappable();
        this.status = BookStatus.from(request.status());
        this.stock = request.stock();
    }

    @PrePersist
    public void prePersist() {
        this.createAt = LocalDateTime.now();
        this.status = BookStatus.ON_SALE;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateAt = LocalDateTime.now();
    }
}
