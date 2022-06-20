package com.bithumbsystems.persistence.mongodb.care.repository;

import com.bithumbsystems.persistence.mongodb.care.model.entity.LegalCounseling;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LegalCounselingCustomRepository {
  Flux<LegalCounseling> findPageBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword, Pageable pageable);

  Mono<Long> countBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword);

  Flux<LegalCounseling> findBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword);
}
