package com.bithumbsystems.persistence.mongodb.education.repository;

import com.bithumbsystems.persistence.mongodb.education.model.entity.Education;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends ReactiveMongoRepository<Education, String> {
}
