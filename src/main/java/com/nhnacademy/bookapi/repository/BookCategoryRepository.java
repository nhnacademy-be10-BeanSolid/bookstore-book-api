package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.domain.bookcategory.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
    boolean existsByName(String name);
}
