package com.bithumbsystems.persistence.mongodb.board.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BoardCustomRepositoryImpl implements BoardCustomRepository {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Flux<Board> findBySearchText(String boardMasterId, LocalDate startDate, LocalDate endDate, String keyword) {
    Criteria criteria = new Criteria();
    criteria.andOperator(
        where("board_master_id").is(boardMasterId),
        where("create_date").gte(startDate).lt(endDate),
        where("is_use").is(true),
        where("title").regex(".*" + keyword.toLowerCase() + ".*", "i")
    );

    MatchOperation matchOperation = Aggregation.match(criteria);
    LookupOperation lookupOperation = Aggregation.lookup("admin_account", "create_account_id", "_id", "account_docs");
    SortOperation sortOperation = Aggregation.sort(Sort.by("create_date").descending());
    Aggregation aggregation = Aggregation.newAggregation(
        matchOperation,
        lookupOperation,
        sortOperation
    );

    return reactiveMongoTemplate.aggregate(aggregation,"cpc_boards", Board.class);
  }

  @Override
  public Flux<Board> findPageBySearchTextForMain(String boardMasterId, LocalDate fromDate, LocalDate toDate, String keyword, Pageable pageable) {
    return reactiveMongoTemplate.find(getQueryBySearchTextForMain(boardMasterId, fromDate, toDate, keyword).with(pageable), Board.class);
  }

  @Override
  public Mono<Long> countBySearchTextForMain(String boardMasterId, LocalDate fromDate, LocalDate toDate, String keyword) {
    return reactiveMongoTemplate.count(getQueryBySearchTextForMain(boardMasterId, fromDate, toDate, keyword), Board.class);
  }

  private Query getQueryBySearchTextForMain(String boardMasterId, LocalDate fromDate, LocalDate toDate, String keyword) {
    var query = new Query();
    query.addCriteria(
        new Criteria()
            .andOperator(
                where("board_master_id").is(boardMasterId),
                where("create_date").gte(fromDate).lt(toDate),
                where("is_use").is(true),
                where("title").regex(".*" + keyword.toLowerCase() + ".*", "i")
            )
    );
    return query;
  }
}
