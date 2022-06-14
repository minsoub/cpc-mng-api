package com.bithumbsystems.persistence.mongodb.care.repository;

import com.bithumbsystems.persistence.mongodb.care.model.entity.LegalCounseling;
import java.time.LocalDate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface LegalCounselingRepository extends ReactiveMongoRepository<LegalCounseling, Long> {
  @Query("{$and: [{createDate: {$gte: ?0, $lt: ?1}}, {$or: [{$expr: {$eq: ['?2', 'null']}}, {status: ?2}]}, {contents: {$regex: ?3, $options: 'i'}}]}")
  Flux<LegalCounseling> findByCondition(LocalDate fromDate, LocalDate toDate, String status, String keyword);
}
