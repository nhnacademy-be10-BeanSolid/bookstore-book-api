package com.nhnacademy.bookapi.book.domain;

import com.nhnacademy.bookapi.booktag.domain.BookTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "book_tag_map")
@NoArgsConstructor
@AllArgsConstructor
public class BookTagMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_tag_map_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    Book book;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    BookTag tag;

    public BookTagMap(Book book, BookTag tag) {
        this.book = book;
        this.tag = tag;
    }
}
