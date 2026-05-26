package com.dsa.week5board.board.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dsa.week5board.board.dto.BoardCreateRequest;
import com.dsa.week5board.board.dto.BoardResponse;
import com.dsa.week5board.board.dto.BoardSearchRequest;
import com.dsa.week5board.board.dto.CursorResponse;
import com.dsa.week5board.board.service.BoardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Boards", description = "게시글 RESTful API")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시글 등록", description = "게시글을 생성하고 생성된 리소스 URI를 Location 헤더로 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 생성 성공"),
            @ApiResponse(responseCode = "401", description = " 입력값 검증 실패", content = @Content)
    })

    @PostMapping
    public ResponseEntity<BoardResponse> create(@Valid @RequestBody BoardCreateRequest request) {
        BoardResponse response = boardService.create(request);
        URI location = URI.create("/api/boards/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "게시글 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> get(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long id

    ) {
        return ResponseEntity.ok(boardService.findById(id));
    }

    @Operation(
            summary = "게시글 목록 조회",
            description = "검색 조건, ID 필터, 복합 Cursor 페이징을 query string으로 조합합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 qurey parameter", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CursorResponse<BoardResponse>> list(
            @ParameterObject @ModelAttribute BoardSearchRequest SearchRequest,
            @Parameter(description = "조회할 게시글 ID 목록", example = "1")
            @RequestParam(name="ids", required = false) List<Long> ids,
            @Parameter(description = "다음 페이지 조회 기준 ID", example = "4")
            @RequestParam(required = false) Long cursorId,
            @Parameter(description = "다음 페이지 조회 기준 생성 시각", example = "2026-05-26T10:12:59")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
            @Parameter(description = "페이지 크기", example = "2")
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(boardService.list(SearchRequest,ids,cursorId,cursorCreatedAt,size));
    }

    @Operation(summary = "게시글 조회수 증가", description = "Views 값을 DB 원자적 UPDATE로 1 증가시킵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회수 증가 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음", content = @Content)
    })
    @PatchMapping("/{id}/views")
    public ResponseEntity<BoardResponse> increaseViews(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long id
    ) {
        return ResponseEntity.ok(boardService.increaseViews(id));
    }

    @Operation(summary = "게시글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long id
    ) {
        boardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}