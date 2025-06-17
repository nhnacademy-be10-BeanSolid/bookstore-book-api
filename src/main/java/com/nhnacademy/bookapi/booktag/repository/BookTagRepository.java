package com.nhnacademy.bookapi.booktag.repository;

import com.nhnacademy.bookapi.booktag.domain.BookTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookTagRepository extends JpaRepository<BookTag, Long>, CustomBookTagRepository {
    boolean existsBookTagByName(String name);
}
