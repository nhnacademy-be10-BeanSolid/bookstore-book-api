package com.nhnacademy.bookapi.booktag.service.impl;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BookTagMapServiceImpl implements BookTagMapService {

    private final BookTagRepository bookTagRepository;

    private final BookRepository bookRepository;

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

        return bookRepository.findBookTagMapResponseByBookIdAndTagId(bookId, tagId)
                .orElseThrow(() -> new BookTagMapNotFoundException(bookId, tagId));
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
    }
}
