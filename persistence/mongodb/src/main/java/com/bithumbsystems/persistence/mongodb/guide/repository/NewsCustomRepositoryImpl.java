package com.bithumbsystems.persistence.mongodb.guide.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.bithumbsystems.persistence.mongodb.guide.model.entity.News;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NewsCustomRepositoryImpl implements NewsCustomRepository {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Flux<News> findPageBySearchText(LocalDate fromDate, LocalDate toDate, String keyword, Pageable pageable) {
    return reactiveMongoTemplate.find(getQueryBySearchText(fromDate, toDate, keyword).with(pageable), News.class);
  }

  @Override
  public Mono<Long> countBySearchText(LocalDate fromDate, LocalDate toDate, String keyword) {
    return reactiveMongoTemplate.count(getQueryBySearchText(fromDate, toDate, keyword), News.class);
  }

  private Query getQueryBySearchText(LocalDate fromDate, LocalDate toDate, String keyword) {
    var query = new Query();

    query.addCriteria(
        new Criteria()
            .andOperator(
                where("postingDate").gte(fromDate).lt(toDate),
                where("is_use").is(true),
                new Criteria()
                    .orOperator(
                        where("newspaper").regex(".*" + keyword.toLowerCase() + ".*", "i"),
                        where("title").regex(".*" + keyword.toLowerCase() + ".*", "i"),
                        where("linkUrl").regex(".*" + keyword.toLowerCase() + ".*", "i")
                    )
            )
    );
    return query;
  }
}
