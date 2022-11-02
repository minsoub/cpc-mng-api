package com.bithumbsystems.persistence.mongodb.board.repository;

import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BoardCustomRepository {

  Flux<Board> findBySearchText(String boardMasterId, LocalDate startDate, LocalDate endDate, String keyword, List<String> category);

  Flux<Board> findById(Long boardId);

  Flux<Board> findBySearchTextForMain(String boardMasterId, LocalDate startDate, LocalDate endDate, String title);
}
