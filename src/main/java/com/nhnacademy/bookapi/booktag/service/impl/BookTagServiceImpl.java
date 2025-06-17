package com.nhnacademy.bookapi.booktag.service.impl;

import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagUpdateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
import com.nhnacademy.bookapi.booktag.exception.BookTagAlreadyExistsException;
import com.nhnacademy.bookapi.booktag.exception.BookTagNotFoundException;
import com.nhnacademy.bookapi.booktag.repository.BookTagRepository;
import com.nhnacademy.bookapi.booktag.service.BookTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookTagServiceImpl implements BookTagService {
    private final BookTagRepository bookTagRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BookTagResponse> getBookTags() {
        return bookTagRepository.findAllBookTagResponses();
    }

    @Override
    @Transactional(readOnly = true)
    public BookTagResponse getBookTag(Long tagId) {
        return bookTagRepository.findBookTagResponseById(tagId)
                .orElseThrow(() -> new BookTagNotFoundException(tagId));
    }

    @Override
    public BookTagResponse createBookTag(BookTagCreateRequest request) {
        if(existsBookTag(request.name())) {
            throw new BookTagAlreadyExistsException(request.name());
        }

        BookTag saved = bookTagRepository.save(new BookTag(request.name()));

        return bookTagRepository.findBookTagResponseById(saved.getTagId())
                .orElseThrow(() -> new BookTagNotFoundException(saved.getTagId()));
    }

    @Override
    public BookTagResponse updateBookTag(Long tagId ,BookTagUpdateRequest request) {
        BookTag tag = bookTagRepository.findById(tagId)
                .orElseThrow(() -> new BookTagNotFoundException(tagId));
        if(existsBookTag(request.name())) {
            throw new BookTagAlreadyExistsException(request.name());
        }
        tag.setName(request.name());
        return bookTagRepository.findBookTagResponseById(tagId)
                .orElseThrow(() -> new BookTagNotFoundException(tagId));
    }

    @Override
    public void deleteBookTag(Long tagId) {
        if(!existsBookTag(tagId)) {
            throw new BookTagNotFoundException(tagId);
        }
        bookTagRepository.deleteById(tagId);
    }

    @Override
    public boolean existsBookTag(Long tagId) {
        return bookTagRepository.existsById(tagId);
    }

    @Override
    public boolean existsBookTag(String name) {
        return bookTagRepository.existsBookTagByName(name);
    }
}
