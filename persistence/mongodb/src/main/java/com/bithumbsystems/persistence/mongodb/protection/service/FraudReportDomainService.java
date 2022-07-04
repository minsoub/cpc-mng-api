package com.bithumbsystems.persistence.mongodb.protection.service;

import com.bithumbsystems.persistence.mongodb.protection.model.entity.FraudReport;
import com.bithumbsystems.persistence.mongodb.protection.repository.FraudReportCustomRepository;
import com.bithumbsystems.persistence.mongodb.protection.repository.FraudReportRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudReportDomainService {

  private final FraudReportRepository fraudReportRepository;
  private final FraudReportCustomRepository fraudReportCustomRepository;

  /**
   * 사기 신고 목록 조회
   * @param startDate 검색 시작일자
   * @param endDate 검색 종료일자
   * @param status 상태
   * @param keyword 키워드
   * @return
   */
  public Flux<FraudReport> findBySearchText(LocalDate startDate, LocalDate endDate, String status, String keyword) {
    return fraudReportCustomRepository.findBySearchText(startDate, endDate, status, keyword);
  }

  /**
   * 사기 신고 조회
   * @param id ID
   * @return
   */
  public Mono<FraudReport> getFraudReportData(Long id) {
    return fraudReportCustomRepository.findById(id).next();
  }

  /**
   * 사기 신고 수정
   * @param fraudReport 사기 신고
   * @return
   */
  public Mono<FraudReport> updateFraudReport(FraudReport fraudReport) {
    fraudReport.setUpdateDate(LocalDateTime.now());
    return fraudReportRepository.save(fraudReport);
  }
}
