package com.nhnacademy.bookapi.document.repository.impl;

import com.nhnacademy.bookapi.book.domain.response.SimpleBookResponse;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.document.repository.CustomBookDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomBookDocumentRepositoryImpl implements CustomBookDocumentRepository {

    private final ElasticsearchOperations elasticsearchOperations;
    private final BookRepository bookRepository;

    @Override
    public Page<SimpleBookResponse> searchByKeyword(String keyword, Pageable pageable) {

        log.info("검색시작");

        Sort currentSort = pageable.getSort();
        // 보조정렬
        Sort newSort = currentSort.and(Sort.by(Sort.Order.desc("id")));
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);

        log.info("page number: {}", pageable.getPageNumber());
        log.info("page size: {}", pageable.getPageSize());

        // 쿼리 객체
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m
                        .query(keyword)
                        .fields(
                                "title^100",
                                "title.synonym^90",
                                "title.jaso^90",
                                "title.chosung^90",
                                "description^10",
                                "author^10",
                                "publisher^10",
                                "tags^50"
                        )
                ))
                .withPageable(newPageable)
                .build();

        // 검색 결과를 담고있는 컨테이너
        SearchHits<BookDocument> hits = elasticsearchOperations.search(query, BookDocument.class); // 검색 실행(쿼리를 보냄)

        // 현재 페이지의 검색 결과 아이디
        List<String> ids = hits.getSearchHits().stream()
                .map(SearchHit::getId)
                .toList();

//        // 도서 엔티티를 담을 리스트
//        List<Book> books = new ArrayList<>();
//        for (String id : ids) {
//            Long bookId = Long.valueOf(id);
//            Book book = bookRepository.findById(bookId)
//                    .orElseThrow(() -> new BookNotFoundException(bookId));
//            books.add(book);
//        }
//
//        List<SimpleBookResponse> content = books.stream()
//                .map(book -> new SimpleBookResponse(
//                        book.getId(),
//                        book.getTitle(),
//                        book.getAuthor(),
//                        book.getSalePrice(),
//                        book.getStock(),
//                        book.getImage(),
//                        book.getViewCount(),
//                        1L,
//                        30.0
//                ))
//                .toList();

        Map<Long, Book> bookMap = bookRepository.findAllById(ids.stream().map(Long::valueOf).toList())
                .stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));

        List<SimpleBookResponse> content = hits.getSearchHits().stream()
                .map(hit -> {
                    BookDocument doc = hit.getContent();
                    Book book = bookMap.get(Long.valueOf(doc.getId()));
                    return new SimpleBookResponse(
                            book.getId(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getSalePrice(),
                            book.getStock(),
                            book.getImage(),
                            book.getViewCount(),
                            doc.getReviewCount(),
                            doc.getRating()
                    );
                })
                .toList();

        long total = hits.getTotalHits();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void increaseViewCount(String id, Long viewCount) {
        Map<String, Object> updateFields = Map.of("viewCount", viewCount);

        // elasticsearch 에서 부분 업데이트를 수행할 때 사용하는 쿼리 객체
        UpdateQuery updateQuery = UpdateQuery.builder(id)
                .withDocument(Document.from(updateFields))
                .build();

        elasticsearchOperations.update(updateQuery, IndexCoordinates.of("beansolid"));
    }

    @Override
    public Page<SimpleBookResponse> findAllSimpleBookResponses(Pageable pageable) {

        Sort currentSort = pageable.getSort();
        Sort newSort = currentSort.and(Sort.by(Sort.Order.desc("id")));
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.matchAll(m -> m))
                .withPageable(newPageable)
                .build();

        SearchHits<BookDocument> hits = elasticsearchOperations.search(query, BookDocument.class);

        List<String> ids = hits.getSearchHits().stream()
                .map(SearchHit::getId)
                .toList();

        Map<Long, Book> bookMap = bookRepository.findAllById(ids.stream().map(Long::valueOf).toList())
                .stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));

        List<SimpleBookResponse> content = hits.getSearchHits().stream()
                .map(hit -> {
                    BookDocument doc = hit.getContent();
                    Book book = bookMap.get(Long.valueOf(doc.getId()));
                    return new SimpleBookResponse(
                            book.getId(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getSalePrice(),
                            book.getStock(),
                            book.getImage(),
                            book.getViewCount(),
                            doc.getReviewCount(),
                            doc.getRating()
                    );
                })
                .toList();

        long total = hits.getTotalHits();

        return new PageImpl<>(content, pageable, total);
    }
}
