package com.bithumbsystems.persistence.mongodb.board.repository;

import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BoardRepository extends ReactiveMongoRepository<Board, Long> {

  @Query("{$and : [{boardMasterId: ?0}, {isUse: ?1}, {title: {$regex: ?2, $options: 'i'}}]}")
  Flux<Board> findByCondition(String boardMasterId, Boolean isUse, String title);
}
