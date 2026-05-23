package com.dsa.week5board.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CursorResponse<T> {

    private List<T> items;
    private Long nextCursorId;
    private LocalDateTime nextCursorCreatedAt;
    private boolean hasNext;
}
