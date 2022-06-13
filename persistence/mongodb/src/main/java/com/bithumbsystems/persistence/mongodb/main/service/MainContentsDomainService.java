package com.bithumbsystems.persistence.mongodb.main.service;

import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import com.bithumbsystems.persistence.mongodb.main.model.entity.MainContents;
import com.bithumbsystems.persistence.mongodb.main.repository.MainContentsRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainContentsDomainService {

  private final MainContentsRepository mainContentsRepository;
  private final String id = "default";

  /**
   * 가상 자산 동향 조회
   * @return
   */
  public Mono<MainContents> findOne() {
    return mainContentsRepository.findById(id);
  }

  /**
   * 메인 컨텐츠 저장
   * @param mainContents 메인 컨텐츠
   * @return
   */
  public Mono<MainContents> save(MainContents mainContents) {
    mainContents.setId(id);
    return mainContentsRepository.save(mainContents);
  }

  /**
   * 메인 컨텐츠 삭제
   * @return
   */
  public Mono<Void> delete() {
    return mainContentsRepository.deleteAll();
  }
}
