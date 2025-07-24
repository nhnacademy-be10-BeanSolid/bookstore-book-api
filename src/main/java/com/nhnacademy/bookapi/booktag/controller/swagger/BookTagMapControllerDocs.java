package com.nhnacademy.bookapi.booktag.controller.swagger;

import com.nhnacademy.bookapi.booktag.domain.request.BookTagMapCreateRequest;
import com.nhnacademy.bookapi.booktag.domain.response.BookTagMapResponse;
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
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "도서-태그 매핑 API", description = "도서와 태그 매핑 관련 API 명세")
public interface BookTagMapControllerDocs {

    @Operation(
            summary = "도서 태그 매핑 조회",
            description = "특정 도서(bookId)에 매핑된 도서 태그 정보를 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BookTagMapResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "해당 도서를 찾을 수 없음"
    )
    ResponseEntity<BookTagMapResponse> getBookTagMapResponse(
            @Parameter(description = "도서 ID", required = true)
            @PathVariable Long bookId
    );

    @Operation(
            summary = "도서 태그 매핑 생성",
            description = "특정 도서(bookId)에 도서 태그 매핑을 생성합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "매핑 생성 성공",
            content = @Content(schema = @Schema(implementation = BookTagMapResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = ValidationFailedException.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    ResponseEntity<BookTagMapResponse> createBookTagMap(
            @Parameter(description = "인증된 사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId,

            @Parameter(description = "도서 ID", required = true)
            @PathVariable Long bookId,

            @Parameter(description = "도서 태그 매핑 생성 요청 DTO", required = true)
            @RequestBody @Valid BookTagMapCreateRequest request,

            BindingResult bindingResult
    );


    @Operation(
            summary = "도서 태그 매핑 삭제",
            description = "특정 도서와 도서 태그간의 매핑을 삭제합니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "매핑 삭제 성공"
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음"
    )
    @ApiResponse(
            responseCode = "404",
            description = "도서 또는 태그 매핑을 찾을 수 없습니다"
    )
    ResponseEntity<Void> deleteBookTagMap(
            @Parameter(description = "인증된 사용자 ID", required = true)
            @RequestHeader("X-USER-ID") String xUserId,

            @Parameter(description = "도서 ID", required = true)
            @PathVariable Long bookId,

            @Parameter(description = "도서 태그 ID", required = true)
            @PathVariable Long tagId
    );

}
