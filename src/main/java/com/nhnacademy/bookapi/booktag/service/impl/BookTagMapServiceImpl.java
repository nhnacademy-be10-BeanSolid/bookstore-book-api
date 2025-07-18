package com.nhnacademy.bookapi.booktag.service.impl;

import com.nhnacademy.bookapi.adpater.service.UserService;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.exception.BookTagMapAlreadyExistsException;
import com.nhnacademy.bookapi.booktag.exception.BookTagMapNotFoundException;
import com.nhnacademy.bookapi.booktag.exception.BookTagNotFoundException;
import com.nhnacademy.bookapi.booktag.repository.BookTagRepository;
import com.nhnacademy.bookapi.booktag.service.BookTagMapService;
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.document.repository.BookDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookTagMapServiceImpl implements BookTagMapService {

    private final BookRepository bookRepository;
    private final BookTagRepository bookTagRepository;
    private final BookDocumentRepository bookDocumentRepository;
    private final UserService userService;


    // 도서에 태그 추가
    @Override
    public BookTagMapResponse createBookTag(Long bookId, BookTagMapCreateRequest request) {
        Long tagId = request.tagId();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        BookTag bookTag = bookTagRepository.findById(tagId)
                .orElseThrow(() -> new BookTagNotFoundException(tagId));

        if (book.getBookTags().contains(bookTag)) {
            throw new BookTagMapAlreadyExistsException(bookId, request.tagId());
        }

        book.getBookTags().add(bookTag);
        bookRepository.save(book);

        // 인덱스 최신화
        Long reviewCount = userService.countReviewsByBookId(bookId);
        Double rating = userService.getAverageEvaluationScoreByBookId(bookId);
        bookDocumentRepository.save(BookDocument.from(book, reviewCount, rating));

        return getBookTagMapResponse(bookId);
    }

    // 도서 태그 삭제
    @Override
    public void deleteBookTag(Long bookId, Long tagId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        BookTag bookTag = bookTagRepository.findById(tagId)
                .orElseThrow(() -> new BookTagNotFoundException(tagId));

        if (!book.getBookTags().contains(bookTag)) {
            throw new BookTagMapNotFoundException(bookId, tagId);
        }

        book.getBookTags().remove(bookTag);

        bookRepository.save(book);
        Long reviewCount = userService.countReviewsByBookId(bookId);
        Double rating = userService.getAverageEvaluationScoreByBookId(bookId);
        bookDocumentRepository.save(BookDocument.from(book, reviewCount, rating));
    }

    // 도서에 해당하는 태그 조회
    @Override
    @Transactional(readOnly = true)
    public BookTagMapResponse getBookTagMapResponse(Long bookId) {
        return bookTagRepository.findBookTagMapResponse(bookId);
    }
}
