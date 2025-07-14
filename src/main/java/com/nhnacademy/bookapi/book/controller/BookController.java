package com.nhnacademy.bookapi.book.controller;

import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookStockReduceRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.*;
import com.nhnacademy.bookapi.common.exception.ValidationFailedException;
import com.nhnacademy.bookapi.book.service.BookService;
import com.nhnacademy.bookapi.book.service.BookSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookSearchService naverBookSearchService;

    @GetMapping("/books-search")
    public ResponseEntity<BookSearchResponse> searchBook(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int start) {
        BookSearchResponse response = naverBookSearchService.searchBook(query, start);
        return ResponseEntity.ok(response);
    }

    // 메인페이지 간단 정보
    @GetMapping("/books")
    public ResponseEntity<Page<SimpleBookResponse>> getAllBooks(Pageable pageable) {
        log.info("page number: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        log.info("sort - {}", pageable.getSort());
        Page<SimpleBookResponse> responses = bookService.getAllBooks(pageable);
        log.info("responses - {}", responses);
        return ResponseEntity.ok(responses);
    }

    // 상세 정보, 조회수 증가
    @GetMapping("/books/{id}")
    public ResponseEntity<BookDetailResponse> getBookDetailById(@PathVariable Long id){
        BookDetailResponse response = bookService.getBookDetailResponseByBookId(id);
        bookService.increaseViewCount(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/books")
    public ResponseEntity<BookResponse> createBook(@RequestHeader("X-USER-ID") String userId,
                                                   @Valid @RequestBody BookCreateRequest request,
                                                   BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        log.info("userId = {}", userId);

        BookResponse response = bookService.createBook(request);
        URI location = URI.create("/books/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/books/{bookId}")
    public ResponseEntity<BookDetailResponse> updateBook(@PathVariable Long bookId,
                                                         @Valid @RequestBody BookUpdateRequest request,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        BookDetailResponse response = bookService.updateBook(bookId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

    // 주문 api 전달
    @GetMapping("/books/ids")
    public ResponseEntity<List<BookOrderResponse>> getBookOrderResponse(@RequestParam List<Long> ids) {
        log.info("요청!");
        List<BookOrderResponse> response = bookService.getBookOrderResponseByBookIds(ids);
        return ResponseEntity.ok(response);
    }

    // 재고 최신화
    @PutMapping("/book-reduce")
    public ResponseEntity<Void> stockUpdate(@RequestBody List<BookStockReduceRequest> request,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        bookService.updateBookStock(request);
        return ResponseEntity.ok().build();
    }

    // 엘라스틱 서치
    @GetMapping("/search")
    public ResponseEntity<Page<SimpleBookResponse>> getSimpleBookResponseByKeyword(@RequestParam String keyword, Pageable pageable) {
        Page<SimpleBookResponse> response = bookService.getSimpleBookResponseByKeyword(keyword, pageable);
        return ResponseEntity.ok(response);
    }
}