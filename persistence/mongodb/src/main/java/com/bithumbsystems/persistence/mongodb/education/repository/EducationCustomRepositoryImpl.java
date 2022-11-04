package com.bithumbsystems.persistence.mongodb.education.repository;

import com.bithumbsystems.persistence.mongodb.education.model.entity.Education;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EducationCustomRepositoryImpl implements EducationCustomRepository {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Flux<Education> findBySearchText(LocalDate startDate, LocalDate endDate, String keyword, Boolean isAnswerComplete) {

        Query query = new Query();
        Criteria criteria = new Criteria();

        if (isAnswerComplete != null) {
            criteria.andOperator(
                    where("create_date").gte(startDate).lt(endDate),
                    where("is_answer_complete").is(isAnswerComplete)
            );
            criteria.orOperator(
                    where("name").regex(".*" + keyword + ".*", "i"),
                    where("email").regex(".*" + keyword + ".*", "i"),
                    where("content").regex(".*" + keyword + ".*", "i")
            );
        } else {
            criteria.andOperator(
                    where("create_date").gte(startDate).lt(endDate)
            );
            criteria.orOperator(
                    where("name").regex(".*" + keyword + ".*", "i"),
                    where("email").regex(".*" + keyword + ".*", "i"),
                    where("content").regex(".*" + keyword + ".*", "i")
            );
        }
        query.addCriteria(criteria);
        query.with(Sort.by("create_date").descending());

        return reactiveMongoTemplate.find(query, Education.class);
    }
}
