package com.nhnacademy.bookapi.bookcategory.service.impl;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryMapCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryNotFoundException;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookCategoryMapServiceImpl implements BookCategoryMapService {

    private final BookRepository bookRepository;

    private final BookCategoryRepository bookCategoryRepository;

    // 도서에 카테고리 추가
    @Override
    public BookCategoryMapResponse bookCategoryMapCreate(Long bookId , BookCategoryMapCreateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        BookCategory category = bookCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BookCategoryNotFoundException(request.categoryId()));

        book.getBookCategories().add(category);
        bookRepository.save(book);

        return new BookCategoryMapResponse(bookId, request.categoryId());
    }

    // 도서에서 카테고리 삭제
    @Override
    public void deleteCategory(Long bookId, Long categoryId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        BookCategory category = bookCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BookCategoryNotFoundException(categoryId));

        book.getBookCategories().remove(category);
        bookRepository.save(book);
    }
}
