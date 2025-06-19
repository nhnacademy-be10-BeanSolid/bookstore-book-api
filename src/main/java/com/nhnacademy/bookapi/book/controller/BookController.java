package com.nhnacademy.bookapi.book.controller;

import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.response.BookSearchResponse;
import com.nhnacademy.bookapi.advice.ValidationFailedException;
import com.nhnacademy.bookapi.book.service.BookService;
import com.nhnacademy.bookapi.book.service.BookSearchApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/books/{id}")
    public ResponseEntity<BookDetailResponse> getBookDetailById(@PathVariable Long id){
        BookDetailResponse response = bookService.getBookDetailResponseByBookId(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/authors/{author}")
    public ResponseEntity<Page<BookResponse>> getBooksByAuthor(@PathVariable String author, Pageable pageable) {
        Page<BookResponse> response = bookService.getBooksResponseByAuthor(author, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/publishers/{publisher}")
    public ResponseEntity<Page<BookResponse>> getBooksByPublisher(@PathVariable String publisher, Pageable pageable) {
        Page<BookResponse> response = bookService.getBooksResponseByPublisher(publisher, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/books")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreateRequest request,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/books/{bookId}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long bookId,
                                                   @Valid @RequestBody BookUpdateRequest request,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        BookResponse response = bookService.updateBook(bookId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookResponse>> getBooksResponseByTag(@RequestParam String tag, Pageable pageable) {
        log.info("컨트롤러 시작   Request to get Books by tag {}", tag);
        Page<BookResponse> response = bookService.getBooksResponseByTag(tag, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/searcht")
    public ResponseEntity<Page<BookResponse>> getBooksResponseByTitle(@RequestParam String title, Pageable pageable) {
        Page<BookResponse> response = bookService.getBookResponseByTitle(title, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
