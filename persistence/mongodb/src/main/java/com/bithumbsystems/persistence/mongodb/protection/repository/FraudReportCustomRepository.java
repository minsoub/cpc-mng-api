package com.bithumbsystems.persistence.mongodb.protection.repository;

import com.bithumbsystems.persistence.mongodb.protection.model.entity.FraudReport;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FraudReportCustomRepository {
  Flux<FraudReport> findPageBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword, Pageable pageable);

  Mono<Long> countBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword);

  Flux<FraudReport> findBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword);
}
