package com.nhnacademy.bookapi.event.listener;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.document.repository.BookDocumentRepository;
import com.nhnacademy.bookapi.event.BookCreateEvent;
import com.nhnacademy.bookapi.event.BookDeleteEvent;
import com.nhnacademy.bookapi.event.BookUpdateEvent;
import com.nhnacademy.bookapi.event.BookViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookDocumentEventListener {

    private final BookDocumentRepository bookDocumentRepository;

    // 생성 이벤트
    // 동기
    @EventListener
    public void handleBookCreatedEvent(BookCreateEvent event) {
        // 처리 로직
        Book book = event.getBook();
        BookDocument document = BookDocument.from(book);
        bookDocumentRepository.save(document);
        log.info("Saving BookDocument to Elasticsearch index: beansolid, document id: {}", document.getId());
    }

    // 생성 롤백 이후에 보상
    // 관리자에게 알리기
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void whenBookCreateRollback(BookCreateEvent event) {
        log.info("Book create transaction rolled back for book id: {}", event.getBook().getId());
    }

    // 조회 이벤트 - 카운트 증가
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookViewEvent(BookViewEvent event) {
        Book book = event.getBook();
        BookDocument document = BookDocument.from(book);
        bookDocumentRepository.save(document);
        log.info("Updated BookDocument in Elasticsearch: {}, viewCount -{}", document.getId(), document.getViewCount());
    }

    // 삭제 이벤트
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookDeleteEvent(BookDeleteEvent event) {
        Book book = event.getBook();
        bookDocumentRepository.deleteById(String.valueOf(book.getId()));
        log.info("Deleted BookDocument from Elasticsearch: {}", book.getId());
    }

    // 삭제 롤백 이후 보상
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void whenBookDeleteRollback(BookDeleteEvent event) {
        log.info("Book delete transaction rolled back for book id: {}", event.getBook().getId());
    }

    // 업데이트 이벤트
    @EventListener
    public void handleBookUpdateEvent(BookUpdateEvent event) {
        Book book = event.getBook();
        BookDocument document = BookDocument.from(book, event.getReviewCount(), event.getRating());
        bookDocumentRepository.save(document);
        log.info("Updated BookDocument in Elasticsearch: {}", document.getId());
    }

    // 업데이트 롤백 이후 보상
    // 관리자에게 알리기
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void whenBookUpdateRollback(BookUpdateEvent event) {
        log.info("Book update transaction rolled back for book id: {}", event.getBook().getId());
    }
}
