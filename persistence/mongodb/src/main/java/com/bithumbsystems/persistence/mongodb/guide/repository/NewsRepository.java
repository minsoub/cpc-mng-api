package com.bithumbsystems.persistence.mongodb.guide.repository;

import com.bithumbsystems.persistence.mongodb.guide.entity.News;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface NewsRepository extends ReactiveMongoRepository<News, Long> {
  @Query("{$and : [{postingDate: {$gte:  ?0, $lt:  ?1}}, {isUse: ?2}, {$or : [{newspaper: {$regex: ?3, $options: 'i'}}, {title: {$regex: ?3, $options: 'i'}}, {linkUrl: {$regex: ?3, $options: 'i'}}]}]}")
  Flux<News> findByCondition(LocalDateTime fromDate, LocalDateTime toDate, Boolean isUse, String keyword);

  Mono<News> findByIdAndIsUse(Long id, Boolean isUse);
}
