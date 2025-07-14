package com.nhnacademy.bookapi.document.repository.impl;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import com.nhnacademy.bookapi.book.domain.response.SimpleBookResponse;
import com.nhnacademy.bookapi.book.exception.BookNotFoundException;
import com.nhnacademy.bookapi.book.repository.BookRepository;
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.document.repository.CustomBookDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomBookDocumentRepositoryImpl implements CustomBookDocumentRepository {

    private final ElasticsearchOperations elasticsearchOperations;
    private final BookRepository bookRepository;

    @Override
    public Page<SimpleBookResponse> searchByKeyword(String keyword, Pageable pageable) {

        log.info("검색시작");

        // 정렬 정보
        Sort sort = pageable.getSort();
        List<SortOptions> sortOptionsList = new ArrayList<>();

        if (sort.isUnsorted()) {
            sortOptionsList.add(SortOptions.of(s -> s.field(f -> f.field("id").order(SortOrder.Desc))));
        } else {
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                SortOrder sortOrder = order.isAscending() ? SortOrder.Asc : SortOrder.Desc;

                sortOptionsList.add(SortOptions.of(s -> s.field(f -> f.field(property).order(sortOrder))));
            }
        }

        log.info("정렬 : {}", sortOptionsList.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")));

        // 쿼리 객체
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m
                        .query(keyword)
                        .fields(
                                "title^5",
                                "title.synonym^5",
                                "title.jaso^5",
                                "description^1",
                                "author^3",
                                "publisher^3",
                                "isbn^5",
                                "tags^5"
                        )
                ))
                .withSort(sortOptionsList)
                .withPageable(pageable) // 현재 페이지만
                .build();

        // 검색 결과를 담고있는 컨테이너
        SearchHits<BookDocument> hits = elasticsearchOperations.search(query, BookDocument.class); // 검색 실행(쿼리를 보냄)

        log.info("hits: {}", hits.getSearchHits());

        // 현재 페이지의 검색 결과 아이디
        List<String> ids = hits.getSearchHits().stream()
                .map(SearchHit::getId)
                .toList();

        // 도서 엔티티를 담을 리스트
        List<Book> books = new ArrayList<>();
        for (String id : ids) {
            Long bookId = Long.valueOf(id);
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException(bookId));
            books.add(book);
        }

        List<SimpleBookResponse> content = books.stream()
                .map(book -> new SimpleBookResponse(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getSalePrice(),
                        book.getStock(),
                        book.getImage(),
                        book.getViewCount()
                ))
                .toList();

        long total = hits.getTotalHits();

        return new PageImpl<>(content, pageable, total);
    }
}
