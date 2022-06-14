package com.bithumbsystems.persistence.mongodb.protection.repository;

import com.bithumbsystems.persistence.mongodb.protection.model.entity.FraudReport;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface FraudReportRepository extends ReactiveMongoRepository<FraudReport, Long> {
  @Query("{$and : [{createDate: {$gte:  ?0, $lt:  ?1}}, {$or: [{$where: '?2.length == 0'},{status: {$in :?2}}]}, {$or : [{title: {$regex: ?3, $options: 'i'}}, {contents: {$regex: ?3, $options: 'i'}}]}]}")
  Flux<FraudReport> findByCondition(LocalDateTime fromDate, LocalDateTime toDate, List<String> status, String keyword);
}
