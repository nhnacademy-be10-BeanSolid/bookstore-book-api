package com.nhnacademy.bookapi;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.document.repository.BookDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookIndexingEventListener {

    private final BookDocumentRepository bookDocumentRepository;

    @EventListener
    public void handleBookCreatedEvent(BookCreatedEvent event) {
        // 처리 로직
        Book book = event.getBook();
        BookDocument document = BookDocument.from(book);
        bookDocumentRepository.save(document);
        log.info("Saving BookDocument to Elasticsearch index: beansolid, document id: {}", document.getId());
    }

    // 롤백 이후에 보상
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void whenBookCreateRollback(BookCreatedEvent event) {
        log.info("Book create transaction rolled back for book id: {}", event.getBook().getId());
    }
}
