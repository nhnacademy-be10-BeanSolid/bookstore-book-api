package com.nhnacademy.bookapi.booktag.service.impl;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.exception.BookTagMappingAlreadyExistsException;
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
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        BookTag bookTag = bookTagRepository.findById(request.getTagId())
                .orElseThrow(() -> new BookTagNotFoundException(request.getTagId()));

        if (book.getBookTags().contains(bookTag)) {
            throw new BookTagMappingAlreadyExistsException(bookId, request.getTagId());
        }

        book.getBookTags().add(bookTag);
        bookRepository.save(book);

        return new BookTagMapResponse(bookId, request.getTagId());
    }

    // 도서 태그 삭제
    @Override
    public void deleteBookTag(Long bookId, Long tagId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        BookTag bookTag = bookTagRepository.findById(tagId)
                .orElseThrow(() -> new BookTagNotFoundException(tagId));

        if (!book.getBookTags().contains(bookTag)) {
            throw new BookTagMappingAlreadyExistsException(bookId, tagId);
        }

        book.getBookTags().remove(bookTag);
        bookRepository.save(book);
    }
}
