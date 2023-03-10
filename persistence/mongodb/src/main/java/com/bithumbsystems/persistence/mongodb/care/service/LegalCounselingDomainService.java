package com.bithumbsystems.persistence.mongodb.care.service;

import com.bithumbsystems.persistence.mongodb.care.model.entity.LegalCounseling;
import com.bithumbsystems.persistence.mongodb.care.repository.LegalCounselingCustomRepository;
import com.bithumbsystems.persistence.mongodb.care.repository.LegalCounselingRepository;
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
public class LegalCounselingDomainService {

  private final LegalCounselingRepository legalCounselingRepository;

  private final LegalCounselingCustomRepository legalCounselingCustomRepository;

  /**
   * 법률 상담 목록 조회
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param status 상태
   * @param keyword 키워드
   * @return
   */
  public Flux<LegalCounseling> findBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword) {
    return legalCounselingCustomRepository.findBySearchText(fromDate, toDate, status, keyword);
  }

  /**
   * 법률 상담 조회
   * @param id ID
   * @return
   */
  public Mono<LegalCounseling> getLegalCounselingData(Long id) {
    return legalCounselingCustomRepository.findById(id).next();
  }

  /**
   * 법률 상담 수정
   * @param legalCounseling 법률 상담
   * @return
   */
  public Mono<LegalCounseling> updateLegalCounseling(LegalCounseling legalCounseling) {
    legalCounseling.setUpdateDate(LocalDateTime.now());
    legalCounseling.setFileDocs(null);
    return legalCounselingRepository.save(legalCounseling);
  }
}
