package com.bithumbsystems.persistence.mongodb.common.repository;

import com.bithumbsystems.persistence.mongodb.common.model.entity.File;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends ReactiveMongoRepository<File, String> {

}
