package com.bithumbsystems.persistence.mongodb.main.service;

import com.bithumbsystems.persistence.mongodb.main.model.entity.MainContents;
import com.bithumbsystems.persistence.mongodb.main.repository.MainContentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainContentsDomainService {

  private final MainContentsRepository mainContentsRepository;

  /**
   * 메인 컨텐츠 조회
   * @param id
   * @return
   */
  public Mono<MainContents> getMainContents(String id) {
    return mainContentsRepository.findById(id);
  }
}
