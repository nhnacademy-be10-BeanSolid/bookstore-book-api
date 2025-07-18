package com.nhnacademy.bookapi.bookcategory.repository;

import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long>, CustomBookCategoryRepository {
    boolean existsByName(String name);

    List<BookCategory> findByParentCategory_CategoryId(Long parentCategoryId);
}
