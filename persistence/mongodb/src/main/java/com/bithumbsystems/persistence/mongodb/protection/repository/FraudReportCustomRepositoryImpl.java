package com.bithumbsystems.persistence.mongodb.protection.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.bithumbsystems.persistence.mongodb.protection.model.entity.FraudReport;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FraudReportCustomRepositoryImpl implements FraudReportCustomRepository {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Flux<FraudReport> findPageBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword, Pageable pageable) {
    return reactiveMongoTemplate.find(getQueryBySearchText(fromDate, toDate, status, keyword).with(pageable), FraudReport.class);
  }

  @Override
  public Mono<Long> countBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword) {
    return reactiveMongoTemplate.count(getQueryBySearchText(fromDate, toDate, status, keyword), FraudReport.class);
  }

  @Override
  public Flux<FraudReport> findBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword) {
    return reactiveMongoTemplate.find(getQueryBySearchText(fromDate, toDate, status, keyword), FraudReport.class);
  }

  private Query getQueryBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword) {
    var query = new Query();

    if (StringUtils.hasLength(status)) {
      query.addCriteria(
          new Criteria()
              .andOperator(
                  where("createDate").gte(fromDate).lt(toDate),
                  where("status").is(status),
                  new Criteria()
                      .orOperator(
                          where("title").regex(".*" + keyword.toLowerCase() + ".*", "i"),
                          where("contents").regex(".*" + keyword.toLowerCase() + ".*", "i")
                      )
              )
      );
    } else {
      query.addCriteria(
          new Criteria()
              .andOperator(
                  where("createDate").gte(fromDate).lt(toDate),
                  new Criteria()
                      .orOperator(
                          where("title").regex(".*" + keyword.toLowerCase() + ".*", "i"),
                          where("contents").regex(".*" + keyword.toLowerCase() + ".*", "i")
                      )
              )
      );
    }
    return query;
  }
}
