    package com.nhnacademy.bookapi.document;

    import com.nhnacademy.bookapi.book.domain.Book;
    import jakarta.persistence.Id;
    import lombok.Getter;
    import lombok.AllArgsConstructor;
    import lombok.ToString;
    import org.springframework.data.elasticsearch.annotations.*;

    @ToString
    @Getter
    @Document(indexName = "beansolid", writeTypeHint = WriteTypeHint.FALSE)
    @AllArgsConstructor
    @Setting(settingPath = "/elasticsearch/settings.json")
    public class BookDocument {

        @Id
        @Field(type = FieldType.Keyword)
        private String id;

        @MultiField(
                mainField = @Field(type = FieldType.Text, analyzer = "korean_icu_analyzer"),
                otherFields = {
                        @InnerField(suffix = "jaso", type = FieldType.Text, analyzer = "jaso_analyzer"),        // 자소
                        @InnerField(suffix = "synonym", type = FieldType.Text, analyzer = "synonym_analyzer") // 동의어 분석기
                }
        )
        private String title;

        @Field(type = FieldType.Text)
        private String description;

        @Field(type = FieldType.Keyword)
        private String author;

        @Field(type = FieldType.Keyword)
        private String publisher;

        @Field(type = FieldType.Keyword)
        private String isbn;

        public static BookDocument from(Book book) {
            return new BookDocument(
                    String.valueOf(book.getId()),
                    book.getTitle(),
                    book.getDescription(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getIsbn()
            );
        }
    }
