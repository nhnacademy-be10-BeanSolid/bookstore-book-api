package com.nhnacademy.bookapi.book.controller;

import com.nhnacademy.bookapi.adpater.service.NaverBookService;
import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.response.BookSearchResponse;
import com.nhnacademy.bookapi.book.domain.response.SimpleBookResponse;
import com.nhnacademy.bookapi.book.service.BookService;
import com.nhnacademy.bookapi.common.annotation.AuthenticatedUserId;
import com.nhnacademy.bookapi.common.exception.ValidationFailedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    private final BookService bookService;
    private final NaverBookService naverBookSearchService;

    @GetMapping("/search")
    public ResponseEntity<BookSearchResponse> searchBook(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int start) {
        BookSearchResponse response = naverBookSearchService.searchBook(query, start);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{book-id}")
    public ResponseEntity<BookDetailResponse> getBookDetailById(@PathVariable(name = "book-id") Long id){
        BookDetailResponse response = bookService.getBookDetailResponseByBookId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@AuthenticatedUserId String userId,
                                                   @Valid @RequestBody BookCreateRequest request,
                                                   BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        BookResponse response = bookService.createBook(request);
        URI location = URI.create("/books/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{book-id}")
    public ResponseEntity<BookDetailResponse> updateBook(@AuthenticatedUserId String userId,
                                                         @PathVariable("book-id") Long bookId,
                                                         @Valid @RequestBody BookUpdateRequest request,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        BookDetailResponse response = bookService.updateBook(bookId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{book-id}")
    public ResponseEntity<Void> deleteBook(@AuthenticatedUserId String userId,
                                           @PathVariable("book-id") Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<SimpleBookResponse>> getAllBooks(Pageable pageable) {
        Page<SimpleBookResponse> response = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(response);
    }
}
