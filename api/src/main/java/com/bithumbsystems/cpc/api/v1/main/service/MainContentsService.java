package com.bithumbsystems.cpc.api.v1.main.service;

import com.bithumbsystems.cpc.api.v1.main.mapper.MainContentsMapper;
import com.bithumbsystems.cpc.api.v1.main.model.response.MainContentsResponse;
import com.bithumbsystems.persistence.mongodb.main.service.MainContentsDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainContentsService {

  private final MainContentsDomainService mainContentsDomainService;

  /**
   * 메인 컨텐츠 조회
   * @param id
   * @return
   */
  public Mono<MainContentsResponse> getMainContents(String id) {
    return mainContentsDomainService.getMainContents(id).map(MainContentsMapper.INSTANCE::toDto);
  }
}
