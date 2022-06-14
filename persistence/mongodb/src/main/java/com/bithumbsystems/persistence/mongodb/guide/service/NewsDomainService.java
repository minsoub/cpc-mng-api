package com.bithumbsystems.persistence.mongodb.guide.service;

import com.bithumbsystems.persistence.mongodb.guide.model.entity.News;
import com.bithumbsystems.persistence.mongodb.guide.repository.NewsRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsDomainService {

  private final NewsRepository newsRepository;

  /**
   * 블록체인 뉴스 등록
   * @param news 블록체인 뉴스
   * @return
   */
  public Mono<News> createNews(News news) {
    news.setIsUse(true);
    news.setCreateDate(LocalDateTime.now());
    return newsRepository.insert(news);
  }

  /**
   * 블록체인 뉴스 목록 조회
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param keyword 키워드
   * @return
   */
  public Flux<News> getNewsList(LocalDate fromDate, LocalDate toDate, String keyword) {
    Boolean isUse = true;
    return newsRepository.findByCondition(fromDate, toDate, isUse, keyword);
  }

  /**
   * 블록체인 뉴스 조회
   * @param id ID
   * @return
   */
  public Mono<News> getNewsData(Long id) {
    Boolean isUse = true;
    return newsRepository.findByIdAndIsUse(id, isUse);
  }

  /**
   * 블록체인 뉴스 수정
   * @param news 블록체인 뉴스
   * @return
   */
  public Mono<News> updateNews(News news) {
    news.setUpdateDate(LocalDateTime.now());
    return newsRepository.save(news);
  }

  /**
   * 블록체인 뉴스 삭제
   * @param news 블록체인 뉴스
   * @return
   */
  public Mono<News> deleteNews(News news) {
    news.setIsUse(false);
    news.setUpdateDate(LocalDateTime.now());
    return newsRepository.save(news);
  }
}
