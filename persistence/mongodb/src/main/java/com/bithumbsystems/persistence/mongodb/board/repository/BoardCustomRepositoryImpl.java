package com.bithumbsystems.persistence.mongodb.board.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BoardCustomRepositoryImpl implements BoardCustomRepository {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Flux<Board> findBySearchText(String boardMasterId, LocalDate startDate, LocalDate endDate, String keyword, String category) {
    Criteria criteria = new Criteria();

    if (StringUtils.hasLength(category)) {
      criteria.andOperator(
          where("board_master_id").is(boardMasterId),
          where("create_date").gte(startDate).lt(endDate),
          where("is_use").is(true),
          where("title").regex(".*" + keyword.toLowerCase() + ".*", "i"),
          where("category").is(category)
      );
    } else {
      criteria.andOperator(
          where("board_master_id").is(boardMasterId),
          where("create_date").gte(startDate).lt(endDate),
          where("is_use").is(true),
          where("title").regex(".*" + keyword.toLowerCase() + ".*", "i")
      );
    }

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
  public Flux<Board> findById(Long boardId) {
    MatchOperation matchOperation = Aggregation.match(where("_id").is(boardId));
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
  public Flux<Board> findBySearchTextForMain(String boardMasterId, LocalDate startDate, LocalDate endDate, String keyword) {
    var query = new Query();
    query.addCriteria(
        new Criteria()
            .andOperator(
                where("board_master_id").is(boardMasterId),
                where("create_date").gte(startDate).lt(endDate),
                where("is_use").is(true),
                where("title").regex(".*" + keyword.toLowerCase() + ".*", "i")
            )
    );
    query.with(Sort.by("create_date").descending());

    return reactiveMongoTemplate.find(query, Board.class);
  }
}
