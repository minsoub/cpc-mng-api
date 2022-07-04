package com.bithumbsystems.persistence.mongodb.protection.repository;

import com.bithumbsystems.persistence.mongodb.protection.model.entity.FraudReport;
import java.time.LocalDate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface FraudReportCustomRepository {
  Flux<FraudReport> findBySearchText(LocalDate startDate, LocalDate endDate, String status, String keyword);

  Flux<FraudReport> findById(Long id);
}
