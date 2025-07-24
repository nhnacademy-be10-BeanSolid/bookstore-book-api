package com.nhnacademy.bookapi.bookcategory.controller.swagger;

import com.nhnacademy.bookapi.bookcategory.domain.request.BookCategoryMapCreateRequest;
import com.nhnacademy.bookapi.bookcategory.domain.response.BookCategoryMapResponse;
import com.nhnacademy.bookapi.common.exception.ValidationFailedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "도서-카테고리 매핑 API", description = "도서와 카테고리 매핑 관련 API 명세")
public interface BookCategoryMapControllerDocs {

    @Operation(summary = "도서의 카테고리 매핑 조회", description = "특정 도서에 매핑된 카테고리 정보를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BookCategoryMapResponse.class)))
    ResponseEntity<BookCategoryMapResponse> getBookCategoryMapResponse(
            @Parameter(description = "도서 ID", required = true) @PathVariable Long bookId
    );

    @Operation(summary = "도서-카테고리 매핑 생성", description = "특정 도서에 새로운 카테고리 매핑을 생성합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "생성 성공",
            content = @Content(schema = @Schema(implementation = BookCategoryMapResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "검증 실패",
            content = @Content(schema = @Schema(implementation = ValidationFailedException.class))
    )
    ResponseEntity<BookCategoryMapResponse> createBookCategoryMap(
            @Parameter(description = "인증된 사용자 ID", required = true) String userId,
            @Parameter(description = "도서 ID", required = true) @PathVariable Long bookId,
            @Parameter(description = "카테고리 매핑 생성 요청 DTO", required = true) @RequestBody @Valid BookCategoryMapCreateRequest request,
            BindingResult bindingResult
    );

    @Operation(
            summary = "도서-카테고리 매핑 삭제",
            description = "특정 도서 ID와 카테고리 ID에 해당하는 매핑을 삭제합니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "삭제 성공"
    )
    @ApiResponse(
            responseCode = "404",
            description = "도서 또는 카테고리 매핑을 찾을 수 없음"
    )
    ResponseEntity<Void> deleteCategoryMap(
            @Parameter(description = "인증된 사용자 ID", required = true) String userId,
            @Parameter(description = "도서 ID", required = true) @PathVariable Long bookId,
            @Parameter(description = "카테고리 ID", required = true) @PathVariable Long categoryId
    );
}
