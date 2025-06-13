package com.nhnacademy.bookapi.book.controller;

import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.BookSearchResponse;
import com.nhnacademy.bookapi.book.exception.ValidationFailedException;
import com.nhnacademy.bookapi.book.service.BookService;
import com.nhnacademy.bookapi.book.service.BookSearchApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        BookDetailResponse response = bookService.getBookDetailByBookId(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/authors/{author}")
    public ResponseEntity<List<BookDetailResponse>> getBooksByAuthor(@PathVariable String author) {
        List<BookDetailResponse> response = bookService.getBooksByAuthor(author);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/publishers/{publisher}")
    public ResponseEntity<List<BookDetailResponse>> getBooksByPublisher(@PathVariable String publisher) {
        List<BookDetailResponse> response = bookService.getBooksByPublisher(publisher);
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
}
