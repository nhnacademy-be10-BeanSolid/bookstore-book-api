package com.nhnacademy.bookapi.book.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.bookapi.adpater.domain.AladinSearchRequest;
import com.nhnacademy.bookapi.adpater.domain.AladinSearchResponse;
import com.nhnacademy.bookapi.adpater.service.AladinService;
import com.nhnacademy.bookapi.adpater.service.UserService;
import com.nhnacademy.bookapi.book.controller.swagger.AdminBookControllerDocs;
import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.response.SimpleBookResponse;
import com.nhnacademy.bookapi.book.service.BookService;
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
public class AdminBookController implements AdminBookControllerDocs {

    private final UserService userService;
    private final BookService bookService;
    private final AladinService aladinService;

    @GetMapping("/search")
    public ResponseEntity<AladinSearchResponse> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") Integer start,
            @RequestParam(name = "MaxResults", defaultValue = "10") Integer maxResults
    ) throws JsonProcessingException {
        AladinSearchRequest request = new AladinSearchRequest(query, start, maxResults);
        return ResponseEntity.ok(aladinService.search(request));
    }

    @GetMapping("/{book-id}")
    public ResponseEntity<BookDetailResponse> getBookDetailById(
            @RequestHeader("X-USER-ID") String xUserId,
            @PathVariable(name = "book-id") Long id){

        userService.getUserAuthorize(xUserId);

        BookDetailResponse response = bookService.getBookDetailResponseByBookId(id);
        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @RequestHeader("X-USER-ID") String xUserId,
            @Valid @RequestBody BookCreateRequest request,
            BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        userService.getUserAuthorize(xUserId);

        BookResponse response = bookService.createBook(request);
        URI location = URI.create("/books/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{book-id}")
    public ResponseEntity<BookDetailResponse> updateBook(
            @RequestHeader("X-USER-ID") String xUserId,
            @PathVariable("book-id") Long bookId,
            @Valid @RequestBody BookUpdateRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        userService.getUserAuthorize(xUserId);

        BookDetailResponse response = bookService.updateBook(bookId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{book-id}")
    public ResponseEntity<Void> deleteBook(
            @RequestHeader("X-USER-ID") String xUserId,
            @PathVariable("book-id") Long bookId) {

        userService.getUserAuthorize(xUserId);
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<SimpleBookResponse>> getAllBooks(Pageable pageable) {
        Page<SimpleBookResponse> response = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(response);
    }
}
