package com.nhnacademy.bookapi.book.controller;

import com.nhnacademy.bookapi.book.controller.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.controller.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.controller.response.BookResponse;
import com.nhnacademy.bookapi.book.exception.ValidationFailedException;
import com.nhnacademy.bookapi.book.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookController {

    private final BookService bookService;

    @GetMapping("/books/{isbn}")
    public ResponseEntity<BookResponse> getBook(@PathVariable String isbn) {
        BookResponse response = bookService.getBook(isbn);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/authors/{author}")
    public ResponseEntity<List<BookResponse>> getBooksByAuthor(@PathVariable String author) {
        List<BookResponse> response = bookService.getBooksByAuthor(author);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/publishers/{publisher}")
    public ResponseEntity<List<BookResponse>> getBooksByPublisher(@PathVariable String publisher) {
        List<BookResponse> response = bookService.getBooksByPublisher(publisher);
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

    @PutMapping("/books/{isbn}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable String isbn,
                                                   @Valid @RequestBody BookUpdateRequest request,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException();
        }
        BookResponse response = bookService.updateBook(isbn, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/books/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        bookService.deleteBook(isbn);
        return ResponseEntity.noContent().build();
    }
}
