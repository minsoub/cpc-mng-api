package com.bithumbsystems.persistence.mongodb.guide.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.bithumbsystems.persistence.mongodb.guide.model.entity.News;
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
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NewsCustomRepositoryImpl implements NewsCustomRepository {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Flux<News> findBySearchText(LocalDate startDate, LocalDate endDate, String keyword) {
    Criteria criteria = new Criteria();
    criteria.andOperator(
        where("posting_date").gte(startDate).lt(endDate),
        where("is_use").is(true),
        new Criteria()
            .orOperator(
                where("newspaper").regex(".*" + keyword.toLowerCase() + ".*", "i"),
                where("title").regex(".*" + keyword.toLowerCase() + ".*", "i"),
                where("linkUrl").regex(".*" + keyword.toLowerCase() + ".*", "i")
            )
    );

    MatchOperation matchOperation = Aggregation.match(criteria);
    LookupOperation lookupOperation = Aggregation.lookup("admin_account", "create_account_id", "_id", "account_docs");
    SortOperation sortOperation = Aggregation.sort(Sort.by("create_date").descending());
    Aggregation aggregation = Aggregation.newAggregation(
        matchOperation,
        lookupOperation,
        sortOperation
    );

    return reactiveMongoTemplate.aggregate(aggregation,"cpc_news", News.class);
  }
}
