package com.nhnacademy.bookapi.bookcategory.repository;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long>, CustomBookCategoryRepository {
    boolean existsByName(String name);


    Optional<BookCategory> findByNameAndParentCategory(String name, BookCategory parentCategory);
}
