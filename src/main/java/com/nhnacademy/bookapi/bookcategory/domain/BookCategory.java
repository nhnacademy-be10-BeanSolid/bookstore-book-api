package com.nhnacademy.bookapi.bookcategory.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
public class BookCategory {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long categoryId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "parent_id")
    private BookCategory parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.REMOVE)
    private List<BookCategory> children = new ArrayList<>();

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    public BookCategory(String name, BookCategory parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
        this.createdAt = LocalDateTime.now();
    }
}
