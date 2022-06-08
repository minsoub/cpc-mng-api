package com.bithumbsystems.persistence.mongodb.care.service;

import com.bithumbsystems.persistence.mongodb.care.entity.LegalCounseling;
import com.bithumbsystems.persistence.mongodb.care.repository.LegalCounselingRepository;
import com.bithumbsystems.persistence.mongodb.common.service.ISequenceGeneratorService;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class LegalCounselingDomainService {

  private final LegalCounselingRepository legalCounselingRepository;
  private final ISequenceGeneratorService sequenceGenerator;

  /**
   * 법률 상담 등록
   * @param legalCounseling 법률 상담
   * @return
   */
  public Mono<LegalCounseling> createLegalCounseling(LegalCounseling legalCounseling) {
    try {
      Long id = sequenceGenerator.generateSequence(LegalCounseling.SEQUENCE_NAME);
      legalCounseling.setId(id);
      legalCounseling.setCreateDate(LocalDateTime.now());
      log.debug("domain service createLegalCounseling legalCounseling => {}", legalCounseling);
      return legalCounselingRepository.save(legalCounseling);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      return Mono.error(e);
    }
  }
}
