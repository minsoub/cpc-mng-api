package com.bithumbsystems.cpc.api.v1.guide.service;

import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.v1.guide.exception.NewsException;
import com.bithumbsystems.cpc.api.v1.guide.mapper.NewsMapper;
import com.bithumbsystems.cpc.api.v1.guide.model.request.NewsRequest;
import com.bithumbsystems.cpc.api.v1.guide.model.response.NewsResponse;
import com.bithumbsystems.persistence.mongodb.guide.model.entity.News;
import com.bithumbsystems.persistence.mongodb.guide.service.NewsDomainService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

  private final NewsDomainService newsDomainService;

  /**
   * 블록체인 뉴스 등록
   * @param newsRequest 블록체인 뉴스
   * @param account 계정
   * @return
   */
  public Mono<NewsResponse> createNews(NewsRequest newsRequest, Account account) {
    News news = NewsMapper.INSTANCE.toEntity(newsRequest);
    news.setCreateAccountId(account.getAccountId());
    return newsDomainService.createNews(news)
        .map(NewsMapper.INSTANCE::toDto)
        .switchIfEmpty(Mono.error(new NewsException(ErrorCode.FAIL_CREATE_CONTENT)));
  }

  /**
   * 블록체인 뉴스 목록 조회
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param keyword 키워드
   * @param pageRequest 페이지 정보
   * @return
   */
  public Mono<Page<News>> getNewsList(LocalDate fromDate, LocalDate toDate, String keyword, PageRequest pageRequest) {
    return newsDomainService.findPageBySearchText(fromDate, toDate, keyword, pageRequest)
        .collectList()
        .zipWith(newsDomainService.countBySearchText(fromDate, toDate, keyword)
            .map(c -> c))
        .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()));
  }

  /**
   * 블록체인 뉴스 조회
   * @param id ID
   * @return
   */
  public Mono<NewsResponse> getNewsData(Long id) {
    return newsDomainService.getNewsData(id).map(NewsMapper.INSTANCE::toDto)
        .switchIfEmpty(Mono.error(new NewsException(ErrorCode.NOT_FOUND_CONTENT)));
  }

  /**
   * 블록체인 뉴스 수정
   * @param newsRequest 블록체인 뉴스
   * @param account 계정
   * @return
   */
  public Mono<NewsResponse> updateNews(NewsRequest newsRequest, Account account) {
    Long id = newsRequest.getId();
    return newsDomainService.getNewsData(id)
        .flatMap(news -> {
          news.setNewspaper(newsRequest.getNewspaper());
          news.setTitle(newsRequest.getTitle());
          news.setLinkUrl(newsRequest.getLinkUrl());
          news.setThumbnailUrl(newsRequest.getThumbnailUrl());
          news.setPostingDate(newsRequest.getPostingDate());
          news.setUpdateAccountId(account.getAccountId());
          return newsDomainService.updateNews(news)
              .map(NewsMapper.INSTANCE::toDto);
        })
        .switchIfEmpty(Mono.error(new NewsException(ErrorCode.FAIL_UPDATE_CONTENT)));
  }

  /**
   * 블록체인 뉴스 삭제
   * @param account 계정
   * @param id ID
   * @return
   */
  public Mono<NewsResponse> deleteNews(Long id, Account account) {
    return newsDomainService.getNewsData(id)
        .flatMap(news -> {
          news.setUpdateAccountId(account.getAccountId());
          return newsDomainService.deleteNews(news);
        })
        .map(NewsMapper.INSTANCE::toDto)
        .switchIfEmpty(Mono.error(new NewsException(ErrorCode.FAIL_DELETE_CONTENT)));
  }
}
