package com.nhnacademy.bookapi.booktag.service;

import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.exception.BookTagAlreadyExistsException;
import com.nhnacademy.bookapi.booktag.exception.BookTagNotFoundException;
import com.nhnacademy.bookapi.booktag.repository.BookTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookTagServiceImpl implements BookTagService {
    private final BookTagRepository bookTagRepository;

    @Override
    public List<BookTag> getBookTags() {
        return bookTagRepository.findAll();
    }

    @Override
    public BookTag getBookTag(Long tagId) {
        return bookTagRepository.findById(tagId).orElseThrow(() -> new BookTagNotFoundException(tagId));
    }

    @Override
    public BookTag createBookTag(BookTag bookTag) {
        if(existsBookTag(bookTag.getName())) {
            throw new BookTagAlreadyExistsException(bookTag.getName());
        }
        return bookTagRepository.save(bookTag);
    }

    @Override
    public BookTag updateBookTag(BookTag bookTag) {
        if(existsBookTag(bookTag.getName())) {
            throw new BookTagAlreadyExistsException(bookTag.getName());
        }
        return bookTagRepository.save(bookTag);
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
