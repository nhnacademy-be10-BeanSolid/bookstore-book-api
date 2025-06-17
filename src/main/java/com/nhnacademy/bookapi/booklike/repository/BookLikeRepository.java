package com.nhnacademy.bookapi.booklike.repository;

import com.nhnacademy.bookapi.booklike.domain.BookLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookLikeRepository extends JpaRepository<BookLike, Long>, CustomBookLikeRepository {

    boolean existsByUserIdAndBookId(String userId, Long bookId);

    boolean existsByBookId(Long bookId);

    void deleteByUserIdAndBookId(String userId, Long bookId);

    void deleteByBookId(Long bookId);
}
