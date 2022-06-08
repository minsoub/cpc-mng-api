package com.bithumbsystems.persistence.mongodb.board.service;

import com.bithumbsystems.persistence.mongodb.board.model.entity.News;
import com.bithumbsystems.persistence.mongodb.board.repository.NewsRepository;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsDomainService {

  private final NewsRepository newsRepository;

  /**
   * 뉴스 목록 조회
   * @param keyword 키워드
   * @return
   */
  public Flux<News> getNewsList(String keyword) {
    return newsRepository.findByTitleContainingIgnoreCaseAndPostingDateGreaterThanEqual(keyword, new Date());
  }
}
