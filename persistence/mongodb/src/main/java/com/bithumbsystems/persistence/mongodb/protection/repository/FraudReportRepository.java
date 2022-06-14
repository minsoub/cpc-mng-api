package com.bithumbsystems.persistence.mongodb.protection.repository;

import com.bithumbsystems.persistence.mongodb.protection.model.entity.FraudReport;
import java.time.LocalDate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface FraudReportRepository extends ReactiveMongoRepository<FraudReport, Long> {
  @Query("{$and: [{createDate: {$gte: ?0, $lt: ?1}}, {$or: [{$expr: {$eq: ['?2', 'null']}}, {status: ?2}]}, {$or: [{title: {$regex: ?3, $options: 'i'}}, {contents: {$regex: ?3, $options: 'i'}}]}]}")
  Flux<FraudReport> findByCondition(LocalDate fromDate, LocalDate toDate, String status, String keyword);
}
