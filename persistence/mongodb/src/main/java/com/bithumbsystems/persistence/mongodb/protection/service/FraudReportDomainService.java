package com.bithumbsystems.persistence.mongodb.protection.service;

import com.bithumbsystems.persistence.mongodb.protection.model.entity.FraudReport;
import com.bithumbsystems.persistence.mongodb.protection.repository.FraudReportCustomRepository;
import com.bithumbsystems.persistence.mongodb.protection.repository.FraudReportRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
   * 사기 신고 등록
   * @param fraudReport 사기 신고
   * @return
   */
  public Mono<FraudReport> createFraudReport(FraudReport fraudReport) {
    fraudReport.setCreateDate(LocalDateTime.now());
    return fraudReportRepository.insert(fraudReport);
  }

  /**
   * 사기 신고 목록 조회(페이징)
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param status 상태
   * @param keyword 키워드
   * @param pageable 페이지 정보
   * @return
   */
  public Flux<FraudReport> findPageBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword, Pageable pageable) {
    return fraudReportCustomRepository.findPageBySearchText(fromDate, toDate, status, keyword, pageable);
  }

  /**
   * 사기 신고 목록 건수 조회
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param status 상태
   * @param keyword 키워드
   * @return
   */
  public Mono<Long> countBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword) {
    return fraudReportCustomRepository.countBySearchText(fromDate, toDate, status, keyword);
  }

  /**
   * 사기 신고 목록 조회
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param status 상태
   * @param keyword 키워드
   * @return
   */
  public Flux<FraudReport> getFraudReportList(LocalDate fromDate, LocalDate toDate, String status, String keyword) {
    return fraudReportCustomRepository.findBySearchText(fromDate, toDate, status, keyword);
  }

  /**
   * 사기 신고 조회
   * @param id ID
   * @return
   */
  public Mono<FraudReport> getFraudReportData(Long id) {
    return fraudReportRepository.findById(id);
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
