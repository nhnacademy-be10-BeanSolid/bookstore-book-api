package com.nhnacademy.bookapi.booktag.controller.swagger;

import com.nhnacademy.bookapi.booktag.domain.request.BookTagCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.request.BookTagUpdateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagResponse;
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

@Tag(name = "태그 API", description = "태그 관련 API 명세")
public interface BookTagControllerDocs {

    @Operation(
            summary = "전체 도서 태그 목록 조회",
            description = "관리자 페이지에서 전체 도서 태그를 페이징 처리하여 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BookTagResponse.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "접근 없음"
    )
    ResponseEntity<Page<BookTagResponse>> getAllBookTags(
            @Parameter(description = "인증된 사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId,

            @Parameter(description = "페이징 정보 (page, size, sort)", required = true)
            Pageable pageable
    );

    @Operation(
            summary = "단일 도서 태그 조회",
            description = "태그 ID를 기반으로 특정 도서 태그 정보를 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BookTagResponse.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    @ApiResponse(
            responseCode = "404",
            description = "도서 태그를 찾을 수 없습니다."
    )
    ResponseEntity<BookTagResponse> getBookTag(
            @Parameter(description = "인증된 사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId,

            @Parameter(description = "조회할 태그 ID", required = true, example = "1")
            @PathVariable Long tagId
    );

    @Operation(
            summary = "도서 태그 생성",
            description = "새로운 도서 태그를 생성합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "생성 성공",
            content = @Content(schema = @Schema(implementation = BookTagResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 유효성 검증 실패"
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    @ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 태그"
    )
    ResponseEntity<BookTagResponse> createBookTag(
            @Parameter(description = "인증된 사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId,

            @Parameter(description = "생성할 태그 요청 데이터", required = true)
            @Valid @RequestBody BookTagCreateRequest request,

            BindingResult bindingResult
    );

    @Operation(
            summary = "도서 태그 수정",
            description = "도서 태그 ID(tagId)를 기준으로 도서 태그 정보를 수정합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "도서 태그 수정 성공",
            content = @Content(schema = @Schema(implementation = BookTagResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "요청 데이터가 유효하지 않음"
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    @ApiResponse(
            responseCode = "404",
            description = "도서 태그를 찾을 수 없습니다"
    )
    ResponseEntity<BookTagResponse> updateBookTag(
            @Parameter(description = "인증된 사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId,

            @Parameter(description = "수정할 도서 태그의 ID", required = true)
            @PathVariable Long tagId,

            @Parameter(description = "도서 태그 수정 요청 DTO", required = true)
            @Valid @RequestBody BookTagUpdateRequest request,

            BindingResult bindingResult
    );


    @Operation(
            summary = "도서 태그 삭제",
            description = "지정한 ID의 도서 태그를 삭제합니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "삭제 성공 (내용 없음)"
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 태그 ID"
    )
    ResponseEntity<Void> deleteBookTag(
            @Parameter(description = "인증된 사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId,

            @Parameter(description = "삭제할 도서 태그의 ID", required = true)
            @PathVariable Long tagId
    );
}
