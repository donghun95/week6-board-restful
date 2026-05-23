package com.dsa.week5board.board.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsa.week5board.board.dto.BoardCreateRequest;
import com.dsa.week5board.board.dto.BoardResponse;
import com.dsa.week5board.board.dto.BoardSearchRequest;
import com.dsa.week5board.board.dto.CursorResponse;
import com.dsa.week5board.board.service.BoardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponse> create(@Valid @RequestBody BoardCreateRequest request) {
        BoardResponse response = boardService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BoardResponse>> search(@ModelAttribute BoardSearchRequest request) {
        return ResponseEntity.ok(boardService.search(request));
    }

    @GetMapping("/bulk")
    public ResponseEntity<List<BoardResponse>> bulk(@RequestParam(name = "ids", required = false) List<Long> ids) {
        return ResponseEntity.ok(boardService.findByIds(ids));
    }

    @GetMapping("/offset")
    public ResponseEntity<List<BoardResponse>> offsetPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(boardService.findOffsetPage(page, size));
    }

    @GetMapping("/cursor")
    public ResponseEntity<CursorResponse<BoardResponse>> cursorPage(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDateTime cursorCreatedAt
            ) {
        return ResponseEntity.ok(boardService.findCursorPage(cursorCreatedAt,cursorId, size));
    }

    @PostMapping("/{id}/views")
    public ResponseEntity<BoardResponse> increaseViews(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.increaseViews(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
