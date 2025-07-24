package com.nhnacademy.bookapi.book.controller.swagger;

import com.nhnacademy.bookapi.book.domain.request.BookCreateRequest;
import com.nhnacademy.bookapi.book.domain.request.BookUpdateRequest;
import com.nhnacademy.bookapi.book.domain.response.BookDetailResponse;
import com.nhnacademy.bookapi.book.domain.response.BookResponse;
import com.nhnacademy.bookapi.book.domain.response.BookSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "관리자 도서 API", description = "관리자용 도서 관련 API 명세")
public interface AdminBookControllerDocs {

    @Operation(summary = "도서 검색", description = "네이버 API를 이용하여 도서를 검색합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "검색 성공",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    ResponseEntity<BookSearchResponse> searchBook(
            @Parameter(description = "인증된 사용자 ID", required = true) @RequestHeader("X-USER-ID") String xUserId,
            @Parameter(description = "검색어", required = true) @RequestParam String query,
            @Parameter(description = "검색 시작 위치", required = false) @RequestParam(defaultValue = "1") int start
    );

    @Operation(summary = "도서 상세 조회", description = "도서 ID로 상세 정보를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    @ApiResponse(
            responseCode = "404",
            description = "도서 없음"
    )
    ResponseEntity<BookDetailResponse> getBookDetailById(
            @Parameter(description = "인증된 사용자 ID", required = true) @RequestHeader("X-USER-ID") String xUserId,
            @Parameter(description = "도서 ID", required = true) @PathVariable("book-id") Long id
    );

    @Operation(summary = "도서 생성", description = "새로운 도서를 등록합니다.")
    @ApiResponse(
            responseCode = "201",
            description = "도서 생성 성공",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터"
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    ResponseEntity<BookResponse> createBook(
            @Parameter(description = "인증된 사용자 ID", required = true) @RequestHeader("X-USER-ID") String xUserId,
            @Parameter(description = "도서 생성 요청 DTO", required = true) BookCreateRequest request,
            BindingResult bindingResult
    );

    @Operation(summary = "도서 수정", description = "기존 도서를 수정합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터"
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    @ApiResponse(
            responseCode = "404",
            description = "도서 없음"
    )
    ResponseEntity<BookDetailResponse> updateBook(
            @Parameter(description = "인증된 사용자 ID", required = true) @RequestHeader("X-USER-ID") String xUserId,
            @Parameter(description = "도서 ID", required = true) @PathVariable("book-id") Long bookId,
            @Parameter(description = "도서 수정 요청 DTO", required = true) BookUpdateRequest request,
            BindingResult bindingResult
    );

    @Operation(summary = "도서 삭제", description = "도서 ID로 도서를 삭제합니다.")
    @ApiResponse(
            responseCode = "204",
            description = "삭제 성공"
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    @ApiResponse(
            responseCode = "404",
            description = "도서 없음"
    )
    ResponseEntity<Void> deleteBook(
            @Parameter(description = "인증된 사용자 ID", required = true) @RequestHeader("X-USER-ID") String xUserId,
            @Parameter(description = "도서 ID", required = true) @PathVariable("book-id") Long bookId
    );

}
