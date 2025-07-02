package com.nhnacademy.bookapi.document.repository.impl;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.nhnacademy.bookapi.document.BookDocument;
import com.nhnacademy.bookapi.document.repository.CustomBookDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomBookDocumentRepositoryImpl implements CustomBookDocumentRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<BookDocument> searchByKeyword(String keyword, Pageable pageable) {

        log.info("검색시작");

        // 쿼리 객체
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m
                        .query(keyword)
                        .fields(
                                "title^5",
                                "title.synonym^5",
                                "title.jaso^5",
                                "description^4",
                                "author",
                                "publisher",
                                "isbn^5",
                                "tags^5"
                        )
                ))
                .withSort(s -> s.field(f -> f.field("id").order(SortOrder.Desc)))
                .withPageable(pageable)
                .build();

        log.info("query {}", query);

        // 검색 결과를 담고있는 컨테이너
        SearchHits<BookDocument> hits = elasticsearchOperations.search(query, BookDocument.class); // 검색 실행(쿼리를 보냄)

        log.info("hits: {}", hits.getSearchHits());

        List<BookDocument> content = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        long total = hits.getTotalHits();

        return new PageImpl<>(content, pageable, total);
    }
}
