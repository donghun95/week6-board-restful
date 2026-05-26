package com.dsa.week5board.board.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dsa.week5board.board.domain.Board;
import com.dsa.week5board.board.dto.BoardSearchRequest;

@Mapper
public interface BoardMapper {

    void insert(Board board);

    Optional<Board> findById(Long id);


    List<Board> findCursorPage(@Param("search") BoardSearchRequest SearchRequest,
                               @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
                               @Param("cursorId") Long cursorId,
                               @Param("limit") int limit);

    void increaseViews(@Param("id") Long id);

    int deleteById(Long id);

    List<Board> findByIds(@Param("ids") List<Long> ids);
}