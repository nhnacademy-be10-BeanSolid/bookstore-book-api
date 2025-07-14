package com.nhnacademy.bookapi.book.service;

import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookStockReduceRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookOrderResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.response.SimpleBookResponse;
import com.nhnacademy.bookapi.book.exception.BookAlreadyExistsException;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.exception.BookNotSaleException;
import com.nhnacademy.bookapi.book.exception.InsufficientStockException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.book.service.impl.BookServiceImpl;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.exception.BookCategoryNotFoundException;
import com.nhnacademy.bookapi.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.document.repository.BookDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookCategoryRepository bookCategoryRepository;
    @Mock
    private BookDocumentRepository bookDocumentRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    BookCategory bookCategory;

    @BeforeEach
    void setUp() {
        bookCategory = new BookCategory("소설", null);
    }

    @Test
    @DisplayName("도서 생성")
    void createBook_success() {
        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.of(2020,10,19) , "test000000000", 10000, 5000, false, 100, null, Set.of(1L));
        Book book = Book.from(request, Set.of(bookCategory));
        ReflectionTestUtils.setField(book, "id", 1L);

        when(bookRepository.existsByIsbn("test000000000")).thenReturn(false);
        when(bookCategoryRepository.findById(1L)).thenReturn(Optional.of(bookCategory));

        BookResponse bookResponse = BookResponse.from(book);

        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookRepository.findBookResponseById(1L)).thenReturn(Optional.of(bookResponse));
        when(bookDocumentRepository.save(any(BookDocument.class))).thenReturn(BookDocument.from(book));

        BookResponse response = bookService.createBook(request);

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("타이틀");
        assertThat(response.isbn()).isEqualTo("test000000000");
        assertThat(response.bookCategories()).contains("소설");
        assertThat(response.publishAt()).isEqualTo(LocalDate.of(2020,10,19));
    }

    @Test
    @DisplayName("도서 생성 - 존재하는 도서")
    void createBook_alreadyExists() {
        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789", 10000, 5000, false, 100, null, new HashSet<>());

        when(bookRepository.existsByIsbn("test123456789")).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(BookAlreadyExistsException.class);
    }

    @Test
    @DisplayName("도서 생성 - 존재하지 않은 카테고리 요청")
    void createBook_bookCategoryNotFound() {
        BookCreateRequest request = new BookCreateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.now(), "test123456789", 10000, 5000, false, 100, null, Set.of(99L));

        when(bookRepository.existsByIsbn("test123456789")).thenReturn(false);
        when(bookCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(BookCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("도서 상세정보")
    void getBookDetailResponseByBookId_success() {
        Long id = 1L;
        int likeCount = 2;
        Book book = new Book();
        ReflectionTestUtils.setField(book, "status", BookStatus.ON_SALE);
        BookDetailResponse response = BookDetailResponse.from(book, likeCount);

        when(bookRepository.findBookDetailResponseByBookId(id)).thenReturn(Optional.of(response));

        BookDetailResponse result = bookService.getBookDetailResponseByBookId(id);

        assertThat(result).isNotNull();
        assertThat(result.likeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("도서 상세정보 - 존재하지 않는 도서")
    void getBookDetailResponseByBookId_notFound() {
        when(bookRepository.findBookDetailResponseByBookId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookDetailResponseByBookId(999L))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("조회 카운트 증가")
    void increaseViewCount() {
        bookService.increaseViewCount(1L);

        verify(bookRepository, times(1)).incrementViewCount(1L);
    }

    @Test
    @DisplayName("도서 리스트")
    void getAllBooksTest() {
        SimpleBookResponse response1 = new SimpleBookResponse(1L, "제목", "작가", 1000, 20, null, 0);
        SimpleBookResponse response2 = new SimpleBookResponse(2L, "제목1", "작가1", 500, 30, null, 1);

        Pageable pageable = PageRequest.of(0, 4);

        when(bookRepository.findAllSimpleBookResponses(pageable))
                .thenReturn(new PageImpl<>(List.of(response1, response2), pageable, 2));

        Page<SimpleBookResponse> pageResult = bookService.getAllBooks(pageable);

        assertThat(pageResult).isNotNull();
        assertThat(pageResult).hasSize(2);
        assertThat(pageResult.getContent().get(0)).isEqualTo(response1);
        assertThat(pageResult.getContent().get(1)).isEqualTo(response2);
    }

    @Test
    @DisplayName("도서 업데이트")
    void updateBook_success() {
        Book book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);

        BookUpdateRequest request = new BookUpdateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.of(2020,10,19), 10000, 5000, true, BookStatus.SALE_END.toString(), 100);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        book.updateFrom(request);
        when(bookRepository.findBookDetailResponseByBookId(1L)).thenReturn(Optional.of(BookDetailResponse.from(book, 2)));

        BookDetailResponse result = bookService.updateBook(1L, request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.wrappable()).isTrue();
        assertThat(result.status()).isEqualTo(BookStatus.SALE_END.getLabel());
    }

    @Test
    @DisplayName("도서 업데이트 - 존재하지 않는 도서")
    void updateBook_notFound() {
        BookUpdateRequest request = new BookUpdateRequest("타이틀", "설명", "목차", "출판사", "작가",
                LocalDate.of(2020,10,19), 10000, 5000, true, BookStatus.SALE_END.toString(), 80);

        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(999L, request))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("도서 삭제")
    void deleteBook_success() {
        Book book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        doNothing().when(bookRepository).delete(book);
        doNothing().when(bookDocumentRepository).deleteById(String.valueOf(1L));

        bookService.deleteBook(1L);

        verify(bookDocumentRepository, times(1)).deleteById(String.valueOf(1L));
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("도서 삭제 - 존재하지 않는 도서")
    void deleteBook_notFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(999L))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("도서 검색 - 엘라스틱 서치")
    void getBookSimpleBookResponseByBookKeyWord_success() {
        String keyword = "프랑켄슈타인";

        Pageable pageable = PageRequest.of(0, 4);
        List<SimpleBookResponse> content = List.of(
                new SimpleBookResponse(1L, "프랑켄슈타인", "박사", 10000, 20, null, 1L),
                new SimpleBookResponse(2L, "지식", "프랑켄슈타인", 2000, 30, null, 3L)
        );
        Page<SimpleBookResponse> expectedPage = new PageImpl<>(content, pageable, content.size());

        when(bookDocumentRepository.searchByKeyword(keyword, pageable))
                .thenReturn(expectedPage);

        Page<SimpleBookResponse> result = bookService.getSimpleBookResponseByKeyword(keyword, pageable);
        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(2);

        verify(bookDocumentRepository, times(1)).searchByKeyword(keyword, pageable);
    }

    @Test
    @DisplayName("주문상품 정보 전달")
    void getBookOrderResponseByBookIds_success() {
        Book book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        ReflectionTestUtils.setField(book, "status", BookStatus.ON_SALE);
        List<BookOrderResponse> responses = List.of(
                BookOrderResponse.from(book)
        );

        when(bookRepository.findBookOrderResponsesById(List.of(1L))).thenReturn(responses);

        List<BookOrderResponse> result = bookService.getBookOrderResponseByBookIds(List.of(1L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("주문상품 정보 전달 - 판매종료 상태")
    void getBookOrderResponseByBookIds_notSaleException() {
        Book book = new Book();
        ReflectionTestUtils.setField(book, "status", BookStatus.SALE_END);

        when(bookRepository.findBookOrderResponsesById(List.of(1L))).thenReturn(List.of());

        assertThatThrownBy(() -> bookService.getBookOrderResponseByBookIds(List.of(1L)))
                .isInstanceOf(BookNotSaleException.class);
    }

    @Test
    @DisplayName("재고 차감")
    void updateBookStock_success() {
        Book book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        ReflectionTestUtils.setField(book, "stock", 100);

        List<BookStockReduceRequest> requests = List.of(
                new BookStockReduceRequest(1L, 40)
        );

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.updateBookStock(requests);

        assertThat(book.getStock()).isEqualTo(60);
    }

    @Test
    @DisplayName("재고 차감 - 존재하지 않은 도서 요청")
    void updateBookStock_bookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        List<BookStockReduceRequest> requests = List.of(
                new BookStockReduceRequest(1L, 40)
        );

        assertThatThrownBy(() -> bookService.updateBookStock(requests))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("재고 차감 - 재고 부족")
    void updateBookStock_insufficientStock() {
        Book book = new Book();
        ReflectionTestUtils.setField(book, "stock", 70);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        List<BookStockReduceRequest> requests = List.of(
                new BookStockReduceRequest(1L, 120) // 재고 부족 요청
        );

        assertThatThrownBy(() -> bookService.updateBookStock(requests))
                .isInstanceOf(InsufficientStockException.class);
    }
}

