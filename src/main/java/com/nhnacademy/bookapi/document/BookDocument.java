package com.nhnacademy.bookapi.document;

import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.AllArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@Document(indexName = "beansolid")
@AllArgsConstructor
@Setting(settingPath = "/elasticsearch/settings.json")
public class BookDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "korean_analyzer"),
            otherFields = {
                    @InnerField(suffix = "jaso", type = FieldType.Text, analyzer = "jaso_analyzer"),        // 자소
                    @InnerField(suffix = "synonym", type = FieldType.Text, analyzer = "korean_synonym_analyzer"), // 동의어 분석기
            }
    )
    private String title;

    //        @Field(type = FieldType.Text, analyzer = "korean_analyzer", termVector = TermVector.with_positions_offsets)
    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String description;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String author;

    @Field(type = FieldType.Keyword)
    private String publisher;

    @Field(type = FieldType.Keyword)
    private Set<String> tags;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate publishedAt;

    @Field(type = FieldType.Integer)
    private Integer salePrice;

    @Field(type = FieldType.Long)
    private Long viewCount;

    public static BookDocument from(Book book) {
        Set<String> tags = book.getBookTags()
                .stream()
                .map(BookTag::getName)
                .collect(Collectors.toSet());

        return new BookDocument(
                String.valueOf(book.getId()),
                book.getTitle(),
                book.getDescription(),
                book.getAuthor(),
                book.getPublisher(),
                tags,
                book.getPublishAt(),
                book.getSalePrice(),
                book.getViewCount()
        );
    }
}

