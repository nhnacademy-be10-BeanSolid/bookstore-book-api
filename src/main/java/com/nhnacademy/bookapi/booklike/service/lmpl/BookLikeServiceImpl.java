package com.nhnacademy.bookapi.booklike.service.lmpl;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.booklike.controller.request.BookLikeCreateRequest;
import com.nhnacademy.bookapi.booklike.controller.response.BookLikeResponse;
import com.nhnacademy.bookapi.booklike.domain.BookLike;
import com.nhnacademy.bookapi.booklike.exception.BookLikeAlreadyExistsException;
import com.nhnacademy.bookapi.booklike.exception.BookLikeNotExistsException;
import com.nhnacademy.bookapi.booklike.repository.BookLikeRepository;
import com.nhnacademy.bookapi.booklike.service.BookLikeService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookLikeServiceImpl implements BookLikeService {

    private final BookLikeRepository bookLikeRepository;

    private final BookRepository bookRepository;

    // 도서 좋아요 생성
    @Override
    public BookLikeResponse createBookLike(BookLikeCreateRequest request) {
        String userId = request.getUserId();
        Long bookId = request.getBookId();
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));

        if(bookLikeRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new BookLikeAlreadyExistsException(userId, book.getTitle());
        }

        BookLike bookLike = new BookLike(userId, book);
        bookLikeRepository.save(bookLike);

        return BookLikeResponse.of(bookLike);
    }

    // 유저별 좋아요 조회
    @Override
    @Transactional(readOnly = true)
    public List<BookLikeResponse> getBookLikesByUserId(String userId) {
        List<BookLike> bookLikes = bookLikeRepository.getBookLikesByUserId(userId);
        return bookLikes.stream().map(BookLikeResponse::of).collect(Collectors.toList());
    }

    // 도서별 좋아요 조회
    @Override
    @Transactional(readOnly = true)
    public List<BookLikeResponse> getBookLikesByBookId(Long bookId) {
        if(!bookLikeRepository.existsByBookId(bookId)) {
            throw new BookNotFoundException(bookId);
        }
        List<BookLike> bookLikes = bookLikeRepository.getBookLikesByBookId(bookId);
        return bookLikes.stream().map(BookLikeResponse::of).collect(Collectors.toList());
    }

    // 유저 아이디와 도서 아이디로 좋아요 삭제
    @Override
    public void deleteBookLikeByUserIdAndBookId(String userId, Long bookId) {
        if(!bookLikeRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new BookLikeNotExistsException(userId, bookId);
        }
        bookLikeRepository.deleteByUserIdAndBookId(userId, bookId);
    }

    // 도서 아이디로 삭제
    @Override
    public void deleteBookLikeByBookId(Long bookId) {
        if(!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }
        if(!bookLikeRepository.existsByBookId(bookId)) {
            throw new BookLikeNotExistsException(bookId);
        }
        bookLikeRepository.deleteByBookId(bookId);
    }
}
