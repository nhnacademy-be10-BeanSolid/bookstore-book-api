package com.nhnacademy.bookapi.book.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhnacademy.bookapi.book.domain.Book;
import com.nhnacademy.bookapi.book.domain.BookStatus;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.booktag.domain.BookTag;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record BookDetailResponse(
        Long id,
        String title,
        String description,
        String toc,
        String publisher,
        String author,
        LocalDate publishAt,
        String isbn,
        int originalPrice,
        int salePrice,
        Boolean wrappable,

        LocalDateTime createAt,
        LocalDateTime updateAt,

        BookStatus status,
        int stock,
        String image,

        List<BookCategoryResponse> bookCategories,
        List<BookTagResponse> bookTags,

        int likeCount
) {
    public static BookDetailResponse from (Book book, int likeCount) {
        List<BookCategoryResponse> categories = book.getBookCategories()
                .stream()
                .map(category -> new BookCategoryResponse(
                        category.getCategoryId(),
                        category.getName(),
                        category.getParentCategory() != null ? category.getParentCategory().getCategoryId() : null,
                        category.getParentCategory() != null ? category.getParentCategory().getName() : null,
                        category.getCreatedAt(),
                        category.getUpdatedAt()
                ))
                .sorted(Comparator.comparing(BookCategoryResponse::categoryId))
                .collect(Collectors.toList());

        List<BookTagResponse> tags = book.getBookTags()
                .stream()
                .map(tag -> new BookTagResponse(tag.getTagId(), tag.getName()))
                .sorted(Comparator.comparing(BookTagResponse::tagId))
                .collect(Collectors.toList());

        return new BookDetailResponse(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getToc(),
                book.getPublisher(),
                book.getAuthor(),
                book.getPublishAt(),
                book.getIsbn(),
                book.getOriginalPrice(),
                book.getSalePrice(),
                book.isWrappable(),
                book.getCreateAt(),
                book.getUpdateAt(),
                book.getStatus(),
                book.getStock(),
                book.getImage(),
                categories,
                tags,
                likeCount);
    }
}
