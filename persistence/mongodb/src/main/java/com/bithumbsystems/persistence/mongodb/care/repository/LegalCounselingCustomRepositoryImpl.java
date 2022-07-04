package com.bithumbsystems.persistence.mongodb.care.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.bithumbsystems.persistence.mongodb.care.model.entity.LegalCounseling;
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
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LegalCounselingCustomRepositoryImpl implements LegalCounselingCustomRepository{

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Flux<LegalCounseling> findBySearchText(LocalDate startDate, LocalDate endDate, String status, String keyword) {
    var criteria = new Criteria();

    if (StringUtils.hasLength(status)) {
      criteria.andOperator(
          where("create_date").gte(startDate).lt(endDate),
          where("status").is(status),
          new Criteria()
              .orOperator(
                  where("title").regex(".*" + keyword.toLowerCase() + ".*", "i"),
                  where("contents").regex(".*" + keyword.toLowerCase() + ".*", "i")
              )
      );
    } else {
      criteria.andOperator(
          where("create_date").gte(startDate).lt(endDate),
          new Criteria()
              .orOperator(
                  where("title").regex(".*" + keyword.toLowerCase() + ".*", "i"),
                  where("contents").regex(".*" + keyword.toLowerCase() + ".*", "i")
              )
      );
    }

    MatchOperation matchOperation = Aggregation.match(criteria);
    LookupOperation lookupOperation = Aggregation.lookup("cpc_files", "attach_file_id", "_id", "file_docs");
    SortOperation sortOperation = Aggregation.sort(Sort.by("create_date").descending());
    Aggregation aggregation = Aggregation.newAggregation(
        matchOperation,
        lookupOperation,
        sortOperation
    );

    return reactiveMongoTemplate.aggregate(aggregation,"cpc_legal_counseling", LegalCounseling.class);
  }

  @Override
  public Flux<LegalCounseling> findById(Long id) {
    MatchOperation matchOperation = Aggregation.match(where("_id").is(id));
    LookupOperation lookupOperation = Aggregation.lookup("cpc_files", "attach_file_id", "_id", "file_docs");
    SortOperation sortOperation = Aggregation.sort(Sort.by("create_date").descending());
    Aggregation aggregation = Aggregation.newAggregation(
        matchOperation,
        lookupOperation,
        sortOperation
    );

    return reactiveMongoTemplate.aggregate(aggregation,"cpc_legal_counseling", LegalCounseling.class);
  }
}
