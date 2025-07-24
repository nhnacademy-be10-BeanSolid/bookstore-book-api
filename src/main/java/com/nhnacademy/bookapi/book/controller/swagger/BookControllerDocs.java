package com.nhnacademy.bookapi.book.controller.swagger;

import com.nhnacademy.bookapi.book.domain.request.BookStockReduceRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookOrderResponse;
import com.nhnacademy.bookapi.book.domain.response.SimpleBookResponse;
import com.nhnacademy.bookapi.common.exception.ValidationFailedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "도서 API")
public interface BookControllerDocs {

    @Operation(summary = "전체 도서 리스트 조회", description = "정렬 가능한 전체 도서 리스트를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = SimpleBookResponse.class)))
    ResponseEntity<Page<SimpleBookResponse>> getAllBooks(
            @Parameter(description = "페이지 정보 (page, size, sort)") Pageable pageable
    );

    @Operation(summary = "카테고리별 도서 리스트 조회", description = "카테고리 ID에 해당하는 정렬 가능한 도서 리스트를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = SimpleBookResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "카테고리를 찾을 수 없음"
    )
    ResponseEntity<Page<SimpleBookResponse>> getAllBooksByCategory(
            @Parameter(description = "카테고리 ID", required = true) @PathVariable Long categoryId,
            @Parameter(description = "페이지 정보 (page, size, sort)") Pageable pageable
    );

    @Operation(summary = "도서 상세 조회 및 조회수 증가", description = "도서 ID로 상세 정보를 조회하고 조회수를 증가시킵니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BookDetailResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "도서를 찾을 수 없음"
    )
    ResponseEntity<BookDetailResponse> getBookDetailById(
            @Parameter(description = "도서 ID", required = true) @PathVariable("book-id") Long id
    );

    @Operation(summary = "도서 키워드 검색", description = "키워드로 검색 결과를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "검색 성공",
            content = @Content(schema = @Schema(implementation = SimpleBookResponse.class)))
    ResponseEntity<Page<SimpleBookResponse>> getSimpleBookResponseByKeyword(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword,
            @Parameter(description = "페이지 정보 (page, size, sort)") Pageable pageable
    );

    @Operation(summary = "주문용 도서 정보 리스트 조회", description = "여러 도서 ID로 주문에 필요한 도서 정보를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookOrderResponse.class))))
    ResponseEntity<List<BookOrderResponse>> getBookOrderResponse(
            @Parameter(description = "도서 ID 리스트", required = true) @RequestParam List<Long> ids
    );

    @Operation(summary = "도서 재고 최신화", description = "여러 도서의 재고를 최신화합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "재고 업데이트 성공"
    )
    @ApiResponse(
            responseCode = "400",
            description = "검증 실패, 재고 부족",
            content = @Content(schema = @Schema(implementation = ValidationFailedException.class))
    )
    ResponseEntity<Void> stockUpdate(
            @Parameter(description = "재고 감소 요청 리스트", required = true) @Valid @RequestBody List<BookStockReduceRequest> request,
            BindingResult bindingResult
    );

    @Operation(summary = "리뷰 작성 후 도서 인덱스 최신화", description = "리뷰 작성 시 도서의 인덱스를 최신화합니다.")
    @ApiResponse(
            responseCode = "204",
            description = "인덱스 최신화 성공")
    ResponseEntity<Void> updateBookDocument(
            @Parameter(description = "도서 ID", required = true) @PathVariable Long bookId,
            @Parameter(description = "리뷰 수", required = true) @RequestParam Long reviewCount,
            @Parameter(description = "리뷰 평균 점수", required = true) @RequestParam Double reviewAverage
    );

    @Operation(summary = "도서 제목 조회", description = "도서 ID로 도서 제목을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = String.class)))
    ResponseEntity<String> getTitleByBookId(
            @Parameter(description = "도서 ID", required = true) @PathVariable Long bookId
    );
}
