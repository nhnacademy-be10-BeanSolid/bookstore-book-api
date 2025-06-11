package com.nhnacademy.bookapi.booktagmap.repository;

import com.nhnacademy.bookapi.booktagmap.domain.BookTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookTagMapRepository extends JpaRepository<BookTagMap, Long> {
}
