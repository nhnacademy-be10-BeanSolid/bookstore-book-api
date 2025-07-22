package com.nhnacademy.bookapi.document.repository;

import com.nhnacademy.bookapi.document.BookDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookDocumentRepository extends ElasticsearchRepository<BookDocument, String>, CustomBookDocumentRepository{

}
