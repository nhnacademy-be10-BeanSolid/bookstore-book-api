package com.nhnacademy.bookapi.bookcategory.repository;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long>, CustomBookCategoryRepository {
    boolean existsByName(String name);

    List<BookCategory> findByParentCategory_CategoryId(Long parentCategoryId);

    Optional<BookCategory> findByNameAndParentCategory(String currentName, BookCategory parent);

    Optional<BookCategory> findByName(String categoryName);
}
