package com.dsa.week5board.board.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsa.week5board.board.domain.Board;
import com.dsa.week5board.board.dto.BoardCreateRequest;
import com.dsa.week5board.board.dto.BoardResponse;
import com.dsa.week5board.board.dto.BoardSearchRequest;
import com.dsa.week5board.board.dto.CursorResponse;
import com.dsa.week5board.board.mapper.BoardMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardMapper boardMapper;

    @Transactional
    public BoardResponse create(BoardCreateRequest request) {
        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(request.getWriter())
                .build();

        boardMapper.insert(board);
        return findById(board.getId());
    }

    public BoardResponse findById(Long id) {
        return boardMapper.findById(id)
                .map(BoardResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + id));
    }

    public List<BoardResponse> search(BoardSearchRequest request) {
        return boardMapper.search(request).stream()
                .map(BoardResponse::from)
                .toList();
    }

    public List<BoardResponse> findOffsetPage(int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = normalizeSize(size);
        int offset = normalizedPage * normalizedSize;

        return boardMapper.findOffsetPage(normalizedSize, offset).stream()
                .map(BoardResponse::from)
                .toList();
    }

    public CursorResponse<BoardResponse> findCursorPage(LocalDateTime cursorCreatedAt, Long cursorId, int size) {
        int normalizedSize = normalizeSize(size);
        int limitPlusOne = normalizedSize + 1;

        List<Board> rows = boardMapper.findCursorPage(cursorCreatedAt, cursorId, limitPlusOne);

        boolean hasNext = rows.size() > normalizedSize;
        List<Board> pageItems = hasNext ? rows.subList(0, normalizedSize) : rows;
        Long nextCursorId = hasNext ? pageItems.get(pageItems.size() - 1).getId() : null;
        LocalDateTime nextCursorCreatedAt = hasNext ? pageItems.get(pageItems.size() -1).getCreatedAt() : null;


        return CursorResponse.<BoardResponse>builder()
                .items(pageItems.stream().map(BoardResponse::from).toList())
                .nextCursorId(nextCursorId)
                .nextCursorCreatedAt(nextCursorCreatedAt)
                .hasNext(hasNext)
                .build();
    }

    @Transactional
    public BoardResponse increaseViews(Long id) {
        boardMapper.increaseViews(id);
        return findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        boardMapper.deleteById(id);
    }

    public List<BoardResponse> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return boardMapper.findByIds(ids).stream()
                .map(BoardResponse::from)
                .toList();
    }

    private int normalizeSize(int size) {
        if (size < 1) {
            return 10;
        }
        return Math.min(size, 100);
    }
}
