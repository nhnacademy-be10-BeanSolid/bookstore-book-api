package com.nhnacademy.bookapi.book.controller;

import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookStockReduceRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookOrderResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.response.BookSearchResponse;
import com.nhnacademy.bookapi.advice.ValidationFailedException;
import com.nhnacademy.bookapi.book.service.BookService;
import com.nhnacademy.bookapi.book.service.BookSearchApiService;
import com.nhnacademy.bookapi.document.BookDocument;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookSearchApiService naverBookSearchService;

    @GetMapping("/books-search")
    public ResponseEntity<BookSearchResponse> searchBook(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int start) {
        return ResponseEntity.status(HttpStatus.OK).body(naverBookSearchService.searchBook(query, start));
    }

    @GetMapping("/books")
    public ResponseEntity<Page<BookResponse>> getAllBookResponse(Pageable pageable) {
        Page<BookResponse> responses = bookService.getAllBooks(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookDocument>> getBookDocumentByKeyword(@RequestParam String keyword, Pageable pageable) {
        Page<BookDocument> response = bookService.getBookDocumentByKeyword(keyword, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // /books/ids?ids=
    // 주문 api 전달
    @GetMapping("/books/ids")
    public ResponseEntity<List<BookOrderResponse>> getBookOrderResponse(@RequestParam List<Long> ids) {
        log.info("요청!");
        List<BookOrderResponse> response = bookService.getBookOrderResponseByBookIds(ids);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 재고 최신화
    // 경로를 어떻게?
    @PatchMapping("/book-reduce")
    public ResponseEntity<Void> stockUpdate(@RequestBody List<BookStockReduceRequest> request,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        bookService.updateBookStock(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 도서 세부사항
    @GetMapping("/books/{id}")
    public ResponseEntity<BookDetailResponse> getBookDetailById(@PathVariable Long id){
        BookDetailResponse response = bookService.getBookDetailResponseByBookId(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 도서 생성
    @PostMapping("/books")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreateRequest request,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 도서 업데이트
    @PutMapping("/books/{bookId}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long bookId,
                                                   @Valid @RequestBody BookUpdateRequest request,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        BookResponse response = bookService.updateBook(bookId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 도서 삭제
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}
