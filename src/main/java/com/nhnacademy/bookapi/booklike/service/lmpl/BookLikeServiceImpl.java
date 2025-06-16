package com.nhnacademy.bookapi.booklike.service.lmpl;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.booklike.domain.request.BookLikeCreateRequest;
import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import com.nhnacademy.bookapi.booklike.domain.BookLike;
import com.nhnacademy.bookapi.booklike.exception.BookLikeAlreadyExistsException;
import com.nhnacademy.bookapi.booklike.exception.BookLikeNotFoundException;
import com.nhnacademy.bookapi.booklike.repository.BookLikeRepository;
import com.nhnacademy.bookapi.booklike.service.BookLikeService;
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
    public BookLikeResponse createBookLike(Long bookId, BookLikeCreateRequest request) {
        String userId = request.userId();
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
        return bookLikes.stream().map(BookLikeResponse::of).toList();
    }

    // 도서별 좋아요 조회
    @Override
    @Transactional(readOnly = true)
    public List<BookLikeResponse> getBookLikesByBookId(Long bookId) {
        if(!bookLikeRepository.existsByBookId(bookId)) {
            throw new BookNotFoundException(bookId);
        }
        List<BookLike> bookLikes = bookLikeRepository.getBookLikesByBookId(bookId);
        return bookLikes.stream().map(BookLikeResponse::of).toList();
    }

    // 유저 아이디와 도서 아이디로 좋아요 삭제
    @Override
    public void deleteBookLikeByUserIdAndBookId(String userId, Long bookId) {
        if(!bookLikeRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new BookLikeNotFoundException(userId, bookId);
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
            throw new BookLikeNotFoundException(bookId);
        }
        bookLikeRepository.deleteByBookId(bookId);
    }
}
