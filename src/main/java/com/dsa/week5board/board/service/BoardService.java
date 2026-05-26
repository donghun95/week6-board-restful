package com.dsa.week5board.board.service;

import java.time.LocalDateTime;
import java.util.List;

import com.dsa.week5board.board.exception.BoardNotFoundException;
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
                .orElseThrow(() -> new BoardNotFoundException(id));
    }

    public CursorResponse<BoardResponse> list(
            BoardSearchRequest searchRequest,
            List<Long> ids,
            Long cursorId,
            LocalDateTime cursorCreatedAt,
            int size
    ) {
        if (ids != null && !ids.isEmpty()) {
            List<BoardResponse> items = boardMapper.findByIds(ids).stream()
                    .map(BoardResponse::from)
                    .toList();

            return CursorResponse.<BoardResponse>builder()
                    .items(items)
                    .hasNext(false)
                    .build();
        }

        int normalizedSize = normalizeSize(size);
        int limitPlusOne = normalizedSize + 1;

        List<Board> rows = boardMapper.findCursorPage(searchRequest, cursorCreatedAt, cursorId, limitPlusOne);

        boolean hasNext = rows.size() > normalizedSize;
        List<Board> pageItems = hasNext ? rows.subList(0, normalizedSize) : rows;
        Board lastItem = hasNext ? pageItems.get(pageItems.size() - 1) : null;


        return CursorResponse.<BoardResponse>builder()
                .items(pageItems.stream().map(BoardResponse::from).toList())
                .nextCursorId(lastItem == null ? null : lastItem.getId())
                .nextCursorCreatedAt(lastItem == null ? null : lastItem.getCreatedAt())
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
        int deletedCount = boardMapper.deleteById(id);
        if (deletedCount == 0) {
            throw new BoardNotFoundException(id);
        }
    }

    private int normalizeSize(int size) {
        if (size < 1) {
            return 10;
        }
        return Math.min(size, 100);
    }
}