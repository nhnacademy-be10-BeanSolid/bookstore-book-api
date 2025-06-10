package com.nhnacademy.bookapi.bookLike.repository;

import com.nhnacademy.bookapi.bookLike.domain.BookLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookLikeRepository extends JpaRepository<BookLike, Long> {

    boolean existsByUserIdAndBookId(String userId, Long bookId);

    boolean existsByBookId(Long bookId);

    List<BookLike> getBookLikesByUserId(String userId);

    List<BookLike> getBookLikesByBookId(Long bookId);

    void deleteByUserIdAndBookId(String userId, Long bookId);

    void deleteByBookId(Long bookId);
}
