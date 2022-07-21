package com.bithumbsystems.persistence.mongodb.accesslog.service;

import com.bithumbsystems.persistence.mongodb.accesslog.model.entity.AccessLog;
import com.bithumbsystems.persistence.mongodb.accesslog.repository.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccessLogDomainService {
    private final AccessLogRepository accessLogRepository;

    public Mono<AccessLog> insert(AccessLog log) {
        return accessLogRepository.save(log);
    }

    public Flux<AccessLog> findAll() {
        return accessLogRepository.findAll();
    }
}
