package com.bithumbsystems.persistence.mongodb.care.repository;

import com.bithumbsystems.persistence.mongodb.care.model.entity.LegalCounseling;
import java.time.LocalDate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface LegalCounselingCustomRepository {
  Flux<LegalCounseling> findBySearchText(LocalDate fromDate, LocalDate toDate, String status, String keyword);
  Flux<LegalCounseling> findById(Long id);
}
