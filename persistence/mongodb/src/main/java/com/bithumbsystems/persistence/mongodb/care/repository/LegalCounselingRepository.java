package com.bithumbsystems.persistence.mongodb.care.repository;

import com.bithumbsystems.persistence.mongodb.care.model.entity.LegalCounseling;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalCounselingRepository extends ReactiveMongoRepository<LegalCounseling, Long> {

}
