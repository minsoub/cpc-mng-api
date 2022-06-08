package com.bithumbsystems.persistence.mongodb.board.repository;

import com.bithumbsystems.persistence.mongodb.board.model.entity.News;
import java.util.Date;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface NewsRepository extends ReactiveMongoRepository<News, Long> {
  Flux<News> findByTitleContainingIgnoreCaseAndPostingDateGreaterThanEqual(String keyword, Date date);
}
