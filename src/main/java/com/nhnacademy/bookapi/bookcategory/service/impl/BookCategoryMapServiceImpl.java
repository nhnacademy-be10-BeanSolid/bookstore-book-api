package com.nhnacademy.bookapi.bookcategory.service.impl;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryMapCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryMapAlreadyExistsException;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryMapCreateException;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryMapNotFoundException;
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
    public BookCategoryMapResponse createBookCategoryMap(Long bookId , BookCategoryMapCreateRequest request) {
        Long categoryId = request.categoryId();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        int categoryCount = bookRepository.countBookCategoryByBookId(bookId);
        if (categoryCount >= 10) {
            throw new BookCategoryMapCreateException(bookId, book.getTitle());
        }

        BookCategory category = bookCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BookCategoryNotFoundException(categoryId));

        if (book.getBookCategories().contains(category)) {
            throw new BookCategoryMapAlreadyExistsException(bookId, request.categoryId());
        }

        book.getBookCategories().add(category);
        bookRepository.save(book);

        return bookRepository.findBookCategoryMapResponseByBookIdAndCategoryId(bookId, categoryId)
                .orElseThrow(() -> new BookCategoryMapNotFoundException(bookId,categoryId));
    }

    // 도서에서 카테고리 삭제
    @Override
    public void deleteCategoryMap (Long bookId, Long categoryId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        BookCategory category = bookCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BookCategoryNotFoundException(categoryId));

        if (!book.getBookCategories().contains(category)) {
            throw new BookCategoryMapNotFoundException(bookId, categoryId);
        }

        book.getBookCategories().remove(category);
        bookRepository.save(book);
    }
}
