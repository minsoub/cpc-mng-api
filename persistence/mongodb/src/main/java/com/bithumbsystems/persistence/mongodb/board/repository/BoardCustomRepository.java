package com.bithumbsystems.persistence.mongodb.board.repository;

import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import java.time.LocalDate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BoardCustomRepository {

  Flux<Board> findBySearchText(String boardMasterId, LocalDate startDate, LocalDate endDate, String keyword, String category);

  Flux<Board> findById(Long boardId);

  Flux<Board> findBySearchTextForMain(String boardMasterId, LocalDate fromDate, LocalDate toDate, String title);
}
