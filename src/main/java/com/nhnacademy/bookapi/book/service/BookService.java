package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookStockReduceRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookOrderResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.response.SimpleBookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    BookResponse createBook(BookCreateRequest request);

    BookDetailResponse getBookDetailResponseByBookId(Long id);

    Page<SimpleBookResponse> getAllBooks(Pageable pageable);

    Page<SimpleBookResponse> getAllBooks(Long categoryId, Pageable pageable);

    BookDetailResponse updateBook(Long id, BookUpdateRequest request);

    void deleteBook(Long id);

    // 도서 키워드로 검색
    Page<SimpleBookResponse> getSimpleBookResponseByKeyword(String keyword, Pageable pageable);

    // 주문 api 정보 전달
    List<BookOrderResponse> getBookOrderResponseByBookIds(List<Long> ids);

    // 주문 재고 변경
    void updateBookStock(List<BookStockReduceRequest> requests);

    void increaseViewCount(Long id);

    // 리뷰 작성 후 인덱스 최신화
    void updateBookDocument(Long bookId, Long reviewCount, Double reviewAverage);

    // 유저 서비스에서 필요한 정보 - 도서 아이디로 이름 반환
    String getTitleByBookId(Long bookId);

}
