package com.bithumbsystems.persistence.mongodb.care.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.bithumbsystems.persistence.mongodb.care.model.entity.LegalCounseling;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LegalCounselingCustomRepositoryImpl implements LegalCounselingCustomRepository{

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Flux<LegalCounseling> findBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword) {
    var query = new Query();

    if (StringUtils.hasLength(status)) {
      query.addCriteria(
          new Criteria()
              .andOperator(
                  where("create_date").gte(fromDate).lt(toDate),
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
                  where("create_date").gte(fromDate).lt(toDate),
                  new Criteria()
                      .orOperator(
                          where("title").regex(".*" + keyword.toLowerCase() + ".*", "i"),
                          where("contents").regex(".*" + keyword.toLowerCase() + ".*", "i")
                      )
              )
      );
    }
    query.with(Sort.by("create_date").descending());
    return reactiveMongoTemplate.find(query, LegalCounseling.class);
  }
}
