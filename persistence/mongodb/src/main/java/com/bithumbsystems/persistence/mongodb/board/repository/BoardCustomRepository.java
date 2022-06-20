package com.bithumbsystems.persistence.mongodb.board.repository;

import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BoardCustomRepository {

  Flux<Board> findPageBySearchText(String boardMasterId, String keyword, Pageable pageable);

  Mono<Long> countBySearchText(String boardMasterId, String keyword);

  Flux<Board> findPageBySearchTextForMain(String boardMasterId, LocalDate fromDate, LocalDate toDate, String title, Pageable pageable);

  Mono<Long> countBySearchTextForMain(String boardMasterId, LocalDate fromDate, LocalDate toDate, String title);
}
