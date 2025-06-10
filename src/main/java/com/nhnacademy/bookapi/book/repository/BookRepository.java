package com.nhnacademy.bookapi.book.repository;

import com.nhnacademy.bookapi.book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    boolean existsByAuthor(String author);

    boolean existsByPublisher(String publisher);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAuthor(String author);

    List<Book> findByPublisher(String publisher);
}
