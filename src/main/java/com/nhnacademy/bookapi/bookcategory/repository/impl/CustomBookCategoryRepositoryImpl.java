package com.nhnacademy.bookapi.bookcategory.repository.impl;

import com.nhnacademy.bookapi.book.domain.QBook;
import com.nhnacademy.bookapi.bookcategory.domain.BookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.QBookCategory;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryNodeResponse;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import com.nhnacademy.bookapi.bookcategory.repository.CustomBookCategoryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

@RequiredArgsConstructor
public class CustomBookCategoryRepositoryImpl implements CustomBookCategoryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<BookCategoryResponse> findBookCategoryResponseById(Long id) {
        QBookCategory bookCategory = QBookCategory.bookCategory;
        QBookCategory parent = new QBookCategory("parent");

        BookCategoryResponse result = queryFactory
                .select(Projections.constructor(BookCategoryResponse.class,
                        bookCategory.categoryId,
                        bookCategory.name,
                        parent.categoryId,
                        parent.name,
                        bookCategory.createdAt,
                        bookCategory.updatedAt
                ))
                .from(bookCategory)
                .leftJoin(bookCategory.parentCategory, parent)
                .where(bookCategory.categoryId.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<BookCategoryResponse> findAllBookCategoryResponse(Pageable pageable) {
        QBookCategory bookCategory = QBookCategory.bookCategory;
        QBookCategory parent = new QBookCategory("parent");

        List<BookCategoryResponse> result = queryFactory
                .select(Projections.constructor(BookCategoryResponse.class,
                        bookCategory.categoryId,
                        bookCategory.name,
                        parent.categoryId,
                        parent.name,
                        bookCategory.createdAt,
                        bookCategory.updatedAt
                ))
                .from(bookCategory)
                .leftJoin(bookCategory.parentCategory, parent)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(bookCategory.count())
                .from(bookCategory)
                .fetchOne();

        return new PageImpl<>(result, pageable, total != null ? total : 0);
    }

    @Override
    public BookCategoryMapResponse findBookCategoryMapResponse(Long bookId) {
        QBook book = QBook.book;
        QBookCategory bookCategory = QBookCategory.bookCategory;
        QBookCategory parent = new QBookCategory("parent");

        List<BookCategoryResponse> categories = queryFactory
                .select(Projections.constructor(BookCategoryResponse.class,
                        bookCategory.categoryId,
                        bookCategory.name,
                        parent.categoryId,
                        parent.name,
                        bookCategory.createdAt,
                        bookCategory.updatedAt
                ))
                .from(book)
                .join(book.bookCategories, bookCategory)
                .leftJoin(bookCategory.parentCategory, parent)
                .where(book.id.eq(bookId))
                .fetch();

        return new BookCategoryMapResponse(bookId, categories);
    }

    @Override
    public List<BookCategoryNodeResponse> buildCategoryNode(){
        QBookCategory bookCategory = QBookCategory.bookCategory;
        QBookCategory parent = new QBookCategory("parent");

        List<BookCategoryResponse> result = queryFactory
                .select(Projections.constructor(BookCategoryResponse.class,
                        bookCategory.categoryId,
                        bookCategory.name,
                        parent.categoryId,
                        parent.name,
                        bookCategory.createdAt,
                        bookCategory.updatedAt
                ))
                .from(bookCategory)
                .leftJoin(bookCategory.parentCategory, parent)
                .fetch();

        Map<Long, BookCategoryNodeResponse> nodeMap = new HashMap<>();
        List<BookCategoryNodeResponse> roots = new ArrayList<>();

        // 카테고리 아이디와 노드 dto 맵에 저장
        for (BookCategoryResponse response : result) {
            nodeMap.put(response.categoryId(), BookCategoryNodeResponse.of(response.categoryId(), response.categoryName()));
        }

        // 부모 자식 관계 트리 구조
        for (BookCategoryResponse response : result) {
            Long parentId = response.parentId();
            BookCategoryNodeResponse currentNode = nodeMap.get(response.categoryId());

            if (parentId == null) {
                roots.add(currentNode);
            } else {
                BookCategoryNodeResponse parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    parentNode.children().add(currentNode);
                }
            }
        }
        return roots;
    }
}
