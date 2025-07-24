package com.nhnacademy.bookapi.bookcategory.controller.swagger;

import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryUpdateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryNodeResponse;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Tag(name = "카테고리 API", description = "카테고리 관련 API 명세")
public interface BookCategoryControllerDocs {

    @Operation(
            summary = "전체 카테고리 목록 조회",
            description = "관리자 페이지에 모든 도서 카테고리를 페이징 처리하여 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BookCategoryResponse.class))
    )
    ResponseEntity<Page<BookCategoryResponse>> getAllCategories(
            @Parameter(description = "인증된 사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId,

            @Parameter(description = "페이징 정보 (page, size, sort)", required = true)
            Pageable pageable
    );

    @Operation(summary = "카테고리 단건 조회", description = "카테고리 ID를 이용해 해당 카테고리 정보를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "카테고리 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookCategoryResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "카테고리를 찾을 수 없음"
    )
    ResponseEntity<BookCategoryResponse> getCategoryById(
            @Parameter(description = "인증된 사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId,
            @Parameter(description = "조회할 도서 카테고리의 ID", example = "1")
            @PathVariable("categoryId") Long categoryId
    );

    @Operation(
            summary = "도서 카테고리 생성",
            description = "새로운 도서 카테고리를 생성합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "카테고리 생성 성공",
            content = @Content(schema = @Schema(implementation = BookCategoryResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "검증 실패"
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    ResponseEntity<BookCategoryResponse> createCategory(
            @Parameter(description = "인증된 사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId,

            @Parameter(description = "도서 카테고리 생성 요청 DTO", required = true)
            @Valid @RequestBody BookCategoryCreateRequest request,
            BindingResult bindingResult
    );

    @Operation(
            summary = "도서 카테고리 수정",
            description = "지정한 ID의 도서 카테고리를 수정합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(schema = @Schema(implementation = BookCategoryResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "검증 실패"
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    @ApiResponse(
            responseCode = "404",
            description = "카테고리 없음"
    )
    ResponseEntity<BookCategoryResponse> updateCategory(
            @Parameter(description = "인증된 사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId,

            @Parameter(description = "수정할 도서 카테고리 ID", required = true)
            @PathVariable("categoryId") Long categoryId,

            @Parameter(description = "도서 카테고리 수정 요청 DTO", required = true)
            @Valid @RequestBody BookCategoryUpdateRequest request,

            BindingResult bindingResult
    );

    @Operation(summary = "카테고리 삭제", description = "특정 카테고리를 삭제합니다.")
    @ApiResponse(
            responseCode = "204",
            description = "삭제 성공 - 내용 없음"
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    @ApiResponse(
            responseCode = "404",
            description = "카테고리를 찾을 수 없음"
    )
    ResponseEntity<Void> deleteCategory(
            @Parameter(description = "인증된 사용자 ID", required = true) @RequestHeader("X-USER-ID") String xUserId,
            @Parameter(description = "삭제할 카테고리 ID", required = true) @PathVariable("categoryId") Long categoryId
    );

    @Operation(summary = "카테고리 트리 조회", description = "전체 카테고리를 트리 구조로 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BookCategoryNodeResponse.class))
    )
    ResponseEntity<List<BookCategoryNodeResponse>> getCategoryTree();
}
