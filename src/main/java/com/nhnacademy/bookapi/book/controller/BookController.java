package com.nhnacademy.bookapi.book.controller;

import com.nhnacademy.bookapi.book.domain.request.BookStockReduceRequest;
import com.nhnacademy.bookapi.book.domain.response.*;
import com.nhnacademy.bookapi.common.exception.ValidationFailedException;
import com.nhnacademy.bookapi.book.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 메인페이지 도서 리스트
    @GetMapping("/books")
    public ResponseEntity<Page<SimpleBookResponse>> getAllBooks(Pageable pageable) {
        log.info("page number: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        log.info("sort - {}", pageable.getSort());
        Page<SimpleBookResponse> response = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(response);
    }

    // 카테고리를 가지고 있는 도서 리스트
    @GetMapping("/books/categories/{categoryId}")
    public ResponseEntity<Page<SimpleBookResponse>> getAllBooksByCategory(@PathVariable Long categoryId, Pageable pageable) {
        Page<SimpleBookResponse> response = bookService.getAllBooks(categoryId, pageable);
        return ResponseEntity.ok(response);
    }

    // 상세 정보, 조회수 증가
    @GetMapping("/books/{book-id}")
    public ResponseEntity<BookDetailResponse> getBookDetailById(@PathVariable(name = "book-id") Long id){
        BookDetailResponse response = bookService.getBookDetailResponseByBookId(id);
        bookService.increaseViewCount(id);
        return ResponseEntity.ok(response);
    }

    // 엘라스틱 서치
    @GetMapping("/books/search")
    public ResponseEntity<Page<SimpleBookResponse>> getSimpleBookResponseByKeyword(@RequestParam String keyword,
                                                                                   Pageable pageable) {
        Page<SimpleBookResponse> response = bookService.getSimpleBookResponseByKeyword(keyword, pageable);

        return ResponseEntity.ok(response);
    }

    // 외부 api 에서 호출

    // 주문 api 전달
    @GetMapping("/books/ids")
    public ResponseEntity<List<BookOrderResponse>> getBookOrderResponse(@RequestParam List<Long> ids) {
        log.info("요청!");
        List<BookOrderResponse> response = bookService.getBookOrderResponseByBookIds(ids);
        return ResponseEntity.ok(response);
    }

    // 재고 최신화
    @PutMapping("/book-reduce")
    public ResponseEntity<Void> stockUpdate(@Valid @RequestBody List<BookStockReduceRequest> request,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        bookService.updateBookStock(request);
        return ResponseEntity.ok().build();
    }

    // 유저 api 에서 리뷰 작성시 인덱스 최신화
    @PostMapping("/books/{bookId}/document")
    public ResponseEntity<Void> updateBookDocument(
            @PathVariable Long bookId,
            @RequestParam Long reviewCount,
            @RequestParam Double reviewAverage) {

        bookService.updateBookDocument(bookId, reviewCount, reviewAverage);
        return ResponseEntity.noContent().build();
    }

    // 유저 api 에서 첵 제목 받기
    @GetMapping("/books/{bookId}/title")
    public ResponseEntity<String> getTitleByBookId(@PathVariable Long bookId) {
        String title = bookService.getTitleByBookId(bookId);
        return ResponseEntity.ok(title);
    }
}