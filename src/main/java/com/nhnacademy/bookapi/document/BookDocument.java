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
        private String id;

        @MultiField(
                mainField = @Field(type = FieldType.Text, analyzer = "korean_icu_analyzer"),
                otherFields = {
                        @InnerField(suffix = "synonym", type = FieldType.Text, analyzer = "synonym_analyzer"), // 동의어 분석기
                        @InnerField(suffix = "jaso", type = FieldType.Text, analyzer = "jaso_analyzer")        // 자소
                }
        )
        private String title;

        @Field(type = FieldType.Text)
        private String description;

        private String author;

        private String publisher;

        public static BookDocument from(Book book) {
            return new BookDocument(
                    String.valueOf(book.getId()),
                    book.getTitle(),
                    book.getDescription(),
                    book.getAuthor(),
                    book.getPublisher()
            );
        }
    }
