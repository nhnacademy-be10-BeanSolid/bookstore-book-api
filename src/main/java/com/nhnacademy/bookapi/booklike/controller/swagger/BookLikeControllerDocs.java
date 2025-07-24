package com.nhnacademy.bookapi.booklike.controller.swagger;

import com.nhnacademy.bookapi.booklike.domain.response.BookLikeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "도서 좋아요 API", description = "도서 좋아요 API 명세")
public interface BookLikeControllerDocs {
    @Operation(
            summary = "유저 좋아요 도서 목록 조회",
            description = "유저가 좋아요한 도서 목록을 페이징 처리하여 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BookLikeResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "사용자 없음"
    )
    ResponseEntity<Page<BookLikeResponse>> getBookLikes(
            @Parameter(description = "사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId,

            @Parameter(description = "페이징 정보 (page, size, sort)", required = true)
            Pageable pageable
    );

    @Operation(
            summary = "도서 좋아요 목록 조회",
            description = "특정 도서에 대한 좋아요 목록을 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BookLikeResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "도서를 찾을 수 없음"
    )
    ResponseEntity<Page<BookLikeResponse>> getBookLikesByBookId(
            @Parameter(description = "도서 ID", required = true) @PathVariable Long bookId,
            @Parameter(description = "페이징 정보") Pageable pageable
    );

    @Operation(
            summary = "도서 좋아요 생성",
            description = "특정 도서에 대해 사용자가 좋아요를 생성합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "좋아요 생성 성공",
            content = @Content(schema = @Schema(implementation = BookLikeResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "도서 또는 사용자 없음"
    )
    @ApiResponse(
            responseCode = "409",
            description = "이미 좋아요가 존재하는 경우"
    )
    ResponseEntity<BookLikeResponse> createBookLike(
            @Parameter(description = "도서 ID", required = true)
            @PathVariable Long bookId,

            @Parameter(description = "사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId
    );

    @Operation(
            summary = "도서 좋아요 삭제",
            description = "특정 도서에 대해 사용자가 좋아요를 취소합니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "삭제 성공"
    )
    @ApiResponse(
            responseCode = "404",
            description = "도서 또는 좋아요 정보를 찾을 수 없음"
    )
    ResponseEntity<Void> deleteBookLikeByUserIdAndBookId(
            @Parameter(description = "도서 ID", required = true) @PathVariable Long bookId,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-USER-ID") String xUserId
    );
}
