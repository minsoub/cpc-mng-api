package com.bithumbsystems.persistence.mongodb.guide.repository;

import com.bithumbsystems.persistence.mongodb.guide.model.entity.News;
import java.time.LocalDate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface NewsCustomRepository {
  Flux<News> findBySearchText(LocalDate startDate, LocalDate endDate, String keyword);
}
