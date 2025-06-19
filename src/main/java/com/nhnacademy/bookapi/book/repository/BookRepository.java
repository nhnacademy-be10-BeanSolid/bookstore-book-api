package com.nhnacademy.bookapi.book.repository;

import com.nhnacademy.bookapi.book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, QuerydslPredicateExecutor<Book>, CustomBookRepository {
    boolean existsByIsbn(String isbn);
}

