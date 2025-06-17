package com.nhnacademy.bookapi.book.repository;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.QBook;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomBookRepositoryImpl extends QuerydslRepositorySupport implements CustomBookRepository {

    public CustomBookRepositoryImpl() {
        super(Book.class);
    }

    public Optional<BookResponse> findBookResponseById(Long id) {
        QBook book = QBook.book;

        Book result = from(book)
                .leftJoin(book.bookCategories).fetchJoin()
                .leftJoin(book.bookTags).fetchJoin()
                .where(book.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result).map(BookResponse::of);
    }

    @Override
    public List<BookResponse> findBookResponsesByAuthor(String author) {
        QBook book = QBook.book;

        List<Book> books = from(book)
                .leftJoin(book.bookCategories).fetchJoin()
                .leftJoin(book.bookTags).fetchJoin()
                .where(book.author.eq(author))
                .fetch();

        return books.stream()
                .map(BookResponse::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> findBookDetailByPublisher(String publisher) {
        QBook book = QBook.book;

        List<Book> books = from(book)
                .leftJoin(book.bookCategories).fetchJoin()
                .leftJoin(book.bookTags).fetchJoin()
                .where(book.publisher.eq(publisher))
                .fetch();

        return books.stream()
                .map(BookResponse::of)
                .collect(Collectors.toList());
    }

//    @Override
//    public List<BookTagMapResponse> findBookTagMapResponseByBookId(Long id) {
//        QBook book = QBook.book;
//        QBookTag bookTag = QBookTag.bookTag;
//
//        return queryFactory
//                .select(Projections.constructor(
//                        BookTagMapResponse.class,
//                        book.id,
//                        bookTag.tagId
//                ))
//                .from(book)
//                .join(book.bookTags, bookTag)
//                .where(book.id.eq(id))
//                .fetch();
//    }
}
