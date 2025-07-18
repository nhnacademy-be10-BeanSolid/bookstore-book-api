package com.nhnacademy.bookapi.bookcategory.service.impl;

import com.nhnacademy.bookapi.adpater.service.UserService;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryMapCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.bookcategory.exception.*;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.bookcategory.service.BookCategoryMapService;
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.document.repository.BookDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookCategoryMapServiceImpl implements BookCategoryMapService {

    private final BookRepository bookRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookDocumentRepository bookDocumentRepository;
    private final UserService userService;

    // 도서에 카테고리 추가
    @Override
    public BookCategoryMapResponse createBookCategoryMap(Long bookId , BookCategoryMapCreateRequest request) {
        Long categoryId = request.categoryId();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        int categoryCount = bookRepository.countBookCategoryByBookId(bookId);
        if (categoryCount >= 10) {
            throw new BookCategoryMapCreateException(bookId);
        }

        BookCategory category = bookCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BookCategoryNotFoundException(categoryId));

        if (book.getBookCategories().contains(category)) {
            throw new BookCategoryMapAlreadyExistsException(bookId, request.categoryId());
        }

        book.getBookCategories().add(category);
        bookRepository.save(book);

        // 인덱스 최신화
        Long reviewCount = userService.countReviewsByBookId(bookId);
        Double rating = userService.getAverageEvaluationScoreByBookId(bookId);
        bookDocumentRepository.save(BookDocument.from(book, reviewCount, rating));

        return getBookCategoryMapResponse(bookId);
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

        int categoryCount = bookRepository.countBookCategoryByBookId(bookId);
        if (categoryCount <= 1) {
            throw new BookCategoryMapDeleteFailException();
        }

        book.getBookCategories().remove(category);
        bookRepository.save(book);

        // 인덱스 최신화
        Long reviewCount = userService.countReviewsByBookId(bookId);
        Double rating = userService.getAverageEvaluationScoreByBookId(bookId);
        bookDocumentRepository.save(BookDocument.from(book, reviewCount, rating));
    }

    // 도서의 카테고리 조회
    @Override
    @Transactional(readOnly = true)
    public BookCategoryMapResponse getBookCategoryMapResponse (Long bookId) {
        return bookCategoryRepository.findBookCategoryMapResponse(bookId);
    }
}
