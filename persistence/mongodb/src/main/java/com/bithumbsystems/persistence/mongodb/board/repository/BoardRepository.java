package com.bithumbsystems.persistence.mongodb.board.repository;

import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends ReactiveMongoRepository<Board, Long> {

//  @Query("{$and : [{boardMasterId: ?0}, {isUse: ?1}, {title: {$regex: ?2, $options: 'i'}}]}")
//  Flux<Board> findByCondition(String boardMasterId, Boolean isUse, String title);

//  @Query("{$and : [{boardMasterId: ?0}, {createDate: {$gte:  ?1, $lt:  ?2}}, {isUse: ?3}, {title: {$regex: ?4, $options: 'i'}}]}")
//  Flux<Board> findByConditionForMain(String boardMasterId, LocalDate fromDate, LocalDate toDate, Boolean isUse, String title);
}
