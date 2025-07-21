package com.nhnacademy.bookapi.event;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.document.repository.BookDocumentRepository;
import com.nhnacademy.bookapi.event.listener.BookDocumentEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookDocumentEventListenerTest {

    @Mock
    private BookDocumentRepository bookDocumentRepository;

    @InjectMocks
    private BookDocumentEventListener listener;

    @Test
    void handleBookCreatedEvent_shouldSaveDocument() {
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(1L);

        BookCreateEvent event = new BookCreateEvent(book);
        listener.handleBookCreatedEvent(event);

        verify(bookDocumentRepository, times(1)).save(any(BookDocument.class));
    }

    @Test
    void handleBookViewEvent_shouldUpdateDocument() {
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(1L);

        BookViewEvent event = new BookViewEvent(book);
        listener.handleBookViewEvent(event);

        verify(bookDocumentRepository).save(any(BookDocument.class));
    }

    @Test
    void handleBookDeleteEvent_shouldDeleteDocument() {
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(1L);

        BookDeleteEvent event = new BookDeleteEvent(book);
        listener.handleBookDeleteEvent(event);

        verify(bookDocumentRepository).deleteById("1");
    }

    @Test
    void handleBookUpdateEvent_shouldUpdateDocumentWithReviewAndRating() {
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(1L);

        long reviewCount = 5L;
        double rating = 4.7;

        BookUpdateEvent event = new BookUpdateEvent(book, reviewCount, rating);
        listener.handleBookUpdateEvent(event);

        verify(bookDocumentRepository).save(any(BookDocument.class));
    }

    @Test
    void whenBookCreateRollback_shouldLogMessage() {
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(1L);

        BookCreateEvent event = new BookCreateEvent(book);
        listener.whenBookCreateRollback(event);
    }

    @Test
    void whenBookDeleteRollback_shouldLogMessage() {
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(1L);

        BookDeleteEvent event = new BookDeleteEvent(book);
        listener.whenBookDeleteRollback(event);
    }

    @Test
    void whenBookUpdateRollback_shouldLogMessage() {
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(1L);

        long reviewCount = 5L;
        double rating = 4.7;

        BookUpdateEvent event = new BookUpdateEvent(book, reviewCount, rating);
        listener.whenBookUpdateRollback(event);
    }
}
