package com.bithumbsystems.persistence.mongodb.education.repository;

import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import com.bithumbsystems.persistence.mongodb.education.model.entity.Education;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EducationCustomRepository {
    Flux<Education> findBySearchText(LocalDate startDate, LocalDate endDate, String keyword, Boolean isAnswerComplete);
    Flux<Education> findBySearchAll(LocalDate startDate, LocalDate endDate, String keyword, Boolean isAnswerComplete);
}
