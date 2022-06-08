package com.bithumbsystems.persistence.mongodb.main.repository;

import com.bithumbsystems.persistence.mongodb.main.model.entity.MainContents;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainContentsRepository extends ReactiveMongoRepository<MainContents, String> {

}
