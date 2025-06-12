package com.nhnacademy.bookapi.book.repository;

import com.nhnacademy.bookapi.book.domain.BookTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookTagMapRepository extends JpaRepository<BookTagMap, Long> {

    @Query("select count(btm) > 0 from BookTagMap btm where btm.book.id = :bookId and btm.tag.tagId= :tagId")
    boolean existsByBookIdAndTagId(@Param("bookId") Long bookId, @Param("tagId") Long tagId);

    List<BookTagMap> findByBookId(Long bookId);

    void deleteByBook_IdAndTag_TagId(Long bookId, Long tagId);
}
