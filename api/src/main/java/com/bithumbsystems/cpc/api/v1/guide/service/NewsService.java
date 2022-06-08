package com.bithumbsystems.cpc.api.v1.guide.service;

import com.bithumbsystems.cpc.api.v1.guide.mapper.NewsMapper;
import com.bithumbsystems.cpc.api.v1.guide.model.response.NewsResponse;
import com.bithumbsystems.persistence.mongodb.board.service.NewsDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

  private final NewsDomainService newsDomainService;

  /**
   * 게시글 목록 조회
   * @param keyword 키워드
   * @return
   */
  public Flux<NewsResponse> getNewsList(String keyword) {
    return newsDomainService.getNewsList(keyword).map(NewsMapper.INSTANCE::toDto);
  }
}
