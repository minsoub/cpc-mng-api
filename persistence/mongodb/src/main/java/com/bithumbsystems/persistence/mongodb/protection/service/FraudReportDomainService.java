package com.bithumbsystems.persistence.mongodb.protection.service;

import com.bithumbsystems.persistence.mongodb.guide.entity.News;
import com.bithumbsystems.persistence.mongodb.protection.model.entity.FraudReport;
import com.bithumbsystems.persistence.mongodb.protection.repository.FraudReportRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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

  /**
   * 사기 신고 등록
   * @param fraudReport 사기 신고
   * @return
   */
  public Mono<FraudReport> createFraudReport(FraudReport fraudReport) {
    fraudReport.setCreateDate(LocalDateTime.now());
    return fraudReportRepository.save(fraudReport);
  }

  /**
   * 사기 신고 목록 조회
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param status 상태
   * @param keyword 키워드
   * @return
   */
  public Flux<FraudReport> getFraudReportList(LocalDateTime fromDate, LocalDateTime toDate, String status, String keyword) {
    return fraudReportRepository.findByCondition(fromDate, toDate, Arrays.asList(status), keyword);
  }

  /**
   * 블록체인 뉴스 조회
   * @param id ID
   * @return
   */
  public Mono<FraudReport> getFraudReportData(Long id) {
    Boolean isUse = true;
    return fraudReportRepository.findById(id);
  }

  /**
   * 블록체인 뉴스 수정
   * @param fraudReport 사기 신고
   * @return
   */
  public Mono<FraudReport> updateFraudReport(FraudReport fraudReport) {
    fraudReport.setUpdateDate(LocalDateTime.now());
    return fraudReportRepository.save(fraudReport);
  }
}
