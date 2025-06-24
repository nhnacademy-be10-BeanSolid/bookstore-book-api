package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookStockReduceRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookOrderResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.document.BookDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    BookResponse createBook(BookCreateRequest request);

    BookResponse getBookResponseByBookId(Long id);

    BookDetailResponse getBookDetailResponseByBookId(Long id);

    Page<BookResponse> getAllBooks(Pageable pageable);

    Page<BookResponse> getBooksResponseByAuthor(String author, Pageable pageable);

    Page<BookResponse> getBooksResponseByPublisher(String publisher, Pageable pageable);

    BookResponse updateBook(Long id, BookUpdateRequest request);

    void deleteBook(Long id);

    Page<BookResponse> getBooksResponseByTag(String tag, Pageable pageable);

    // 도서 제목으로 검색
    Page<BookResponse> getBookResponseByTitle(String title, Pageable pageable);

    // 도서 설명으로 검색
    Page<BookResponse> getBookResponseByDescription(String description, Pageable pageable);

    // 도서 키워드로 검색
    Page<BookDocument> getBookDocumentByKeyword(String keyword, Pageable pageable);

    // 주문 api 정보 전달
    List<BookOrderResponse> getBookOrderResponseByBookIds(List<Long> ids);

    // 주문 재고 변경
    void updateBookStock(List<BookStockReduceRequest> requests);
}
