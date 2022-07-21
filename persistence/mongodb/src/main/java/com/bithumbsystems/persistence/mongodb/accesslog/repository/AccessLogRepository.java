package com.bithumbsystems.persistence.mongodb.accesslog.repository;

import com.bithumbsystems.persistence.mongodb.accesslog.model.entity.AccessLog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


public interface AccessLogRepository extends ReactiveMongoRepository<AccessLog, String> {
}
