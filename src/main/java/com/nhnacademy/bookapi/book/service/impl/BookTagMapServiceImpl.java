package com.nhnacademy.bookapi.book.service.impl;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.exception.BookTagNotFoundException;
import com.nhnacademy.bookapi.booktag.repository.BookTagRepository;
import com.nhnacademy.bookapi.book.controller.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.book.controller.response.BookTagMapResponse;
import com.nhnacademy.bookapi.book.domain.BookTagMap;
import com.nhnacademy.bookapi.book.exception.BookTagMappingAlreadyExistsException;
import com.nhnacademy.bookapi.book.exception.BookTagMappingNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookTagMapRepository;
import com.nhnacademy.bookapi.book.service.BookTagMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookTagMapServiceImpl implements BookTagMapService {

    private final BookRepository bookRepository;

    private final BookTagRepository bookTagRepository;

    private final BookTagMapRepository bookTagMapRepository;

    // 도서 태그 저장
    public BookTagMapResponse createBookTagMap(BookTagMapCreateRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException(request.getBookId()));

        BookTag tag = bookTagRepository.findById(request.getTagId())
                .orElseThrow(() -> new BookTagNotFoundException(request.getTagId()));

        // 도서에 해당 태그가 이미 존재하는 경우
        if(bookTagMapRepository.existsByBookIdAndTagId(request.getBookId(), request.getTagId())){
           throw new BookTagMappingAlreadyExistsException(request.getBookId(), request.getTagId());
        }

        BookTagMap bookTagMap = new BookTagMap(book, tag);
        BookTagMap savedBookTagMap = bookTagMapRepository.save(bookTagMap);

        return BookTagMapResponse.of(savedBookTagMap);
    }

    // 도서 아이디로 태그들 찾기
    @Override
    public List<BookTagMapResponse> findBookTagMapByBookId(Long bookId) {
        List<BookTagMap> bookTagMaps = bookTagMapRepository.findByBookId(bookId);
        return bookTagMaps.stream().map(BookTagMapResponse::of).collect(Collectors.toList());
    }

    // 도서 태그 삭제
    public void deleteBookTagMap(Long bookId, Long tagId) {
        if(bookTagMapRepository.existsByBookIdAndTagId(bookId, tagId)){
           throw new BookTagMappingNotFoundException(bookId, tagId);
        }
        bookTagMapRepository.deleteByBook_IdAndTag_TagId(bookId, tagId);
    }
}
