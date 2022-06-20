package com.bithumbsystems.persistence.mongodb.guide.repository;

import com.bithumbsystems.persistence.mongodb.guide.model.entity.News;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface NewsRepository extends ReactiveMongoRepository<News, Long> {
//  @Query("{$and : [{postingDate: {$gte:  ?0, $lt:  ?1}}, {isUse: ?2}, {$or : [{newspaper: {$regex: ?3, $options: 'i'}}, {title: {$regex: ?3, $options: 'i'}}, {linkUrl: {$regex: ?3, $options: 'i'}}]}]}")
//  Flux<News> findByCondition(LocalDate fromDate, LocalDate toDate, Boolean isUse, String keyword);

  Mono<News> findByIdAndIsUse(Long id, Boolean isUse);
}
