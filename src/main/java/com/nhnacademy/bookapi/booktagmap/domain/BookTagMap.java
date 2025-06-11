package com.nhnacademy.bookapi.booktagmap.domain;

import com.nhnacademy.bookapi.book.domain.Book;
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
    @JoinColumn(name = "book_tag_id")
    BookTag tag;



}
