package com.bithumbsystems.persistence.mongodb.protection.service;

import com.bithumbsystems.persistence.mongodb.protection.model.entity.FraudReport;
import com.bithumbsystems.persistence.mongodb.protection.repository.FraudReportRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudReportDomainService {

  private final FraudReportRepository fraudReportRepository;

  /**
   * 사기 신고 등록
   * @param fraudReport 사기 신고
   * @return
   */
  public Mono<FraudReport> createFraudReport(FraudReport fraudReport) {
    fraudReport.setCreateDate(LocalDateTime.now());
    return fraudReportRepository.save(fraudReport);
  }
}
