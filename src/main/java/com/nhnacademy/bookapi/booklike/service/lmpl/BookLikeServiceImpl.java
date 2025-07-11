package com.nhnacademy.bookapi.booklike.service.lmpl;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import com.nhnacademy.bookapi.booklike.domain.BookLike;
import com.nhnacademy.bookapi.booklike.exception.BookLikeAlreadyExistsException;
import com.nhnacademy.bookapi.booklike.exception.BookLikeNotFoundException;
import com.nhnacademy.bookapi.booklike.repository.BookLikeRepository;
import com.nhnacademy.bookapi.booklike.service.BookLikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookLikeServiceImpl implements BookLikeService {

    private final BookLikeRepository bookLikeRepository;

    private final BookRepository bookRepository;

    // 도서 좋아요 생성
    @Override
    public BookLikeResponse createBookLike(Long bookId, String userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if(bookLikeRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new BookLikeAlreadyExistsException(userId, book.getTitle());
        }

        BookLike savedBookLike = bookLikeRepository.save(new BookLike(userId, book));

        return bookLikeRepository.findBookLikeResponseById(savedBookLike.getId())
                .orElseThrow(() -> new BookLikeNotFoundException(savedBookLike.getId()));
    }

    // 유저별 좋아요 조회
    @Override
    @Transactional(readOnly = true)
    public Page<BookLikeResponse> getBookLikesByUserId(String userId, Pageable pageable) {
        return bookLikeRepository.findBookLikeResponsesByUserId(userId, pageable);
    }

    // 도서별 좋아요 조회
    @Override
    @Transactional(readOnly = true)
    public Page<BookLikeResponse> getBookLikesByBookId(Long bookId, Pageable pageable) {
        if(!bookLikeRepository.existsByBookId(bookId)) {
            throw new BookNotFoundException(bookId);
        }
        return bookLikeRepository.findBookLikeResponsesByBookId(bookId, pageable);
    }

    // 유저 아이디와 도서 아이디로 좋아요 삭제
    @Override
    public void deleteBookLikeByUserIdAndBookId(String userId, Long bookId) {
        if(!bookLikeRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new BookLikeNotFoundException(userId, bookId);
        }
        bookLikeRepository.deleteByUserIdAndBookId(userId, bookId);
    }

//    @Override
//    public boolean isUserLikedBook(String userId, Long bookId) {
//        return bookLikeRepository.existsByUserIdAndBookId(userId, bookId);
//    }
}
