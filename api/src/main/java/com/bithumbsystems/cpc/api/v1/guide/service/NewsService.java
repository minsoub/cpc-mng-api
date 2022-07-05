package com.bithumbsystems.cpc.api.v1.guide.service;

import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.v1.guide.exception.NewsException;
import com.bithumbsystems.cpc.api.v1.guide.mapper.NewsMapper;
import com.bithumbsystems.cpc.api.v1.guide.model.request.NewsRequest;
import com.bithumbsystems.cpc.api.v1.guide.model.response.NewsListResponse;
import com.bithumbsystems.cpc.api.v1.guide.model.response.NewsResponse;
import com.bithumbsystems.persistence.mongodb.account.model.entity.AdminAccount;
import com.bithumbsystems.persistence.mongodb.guide.model.entity.News;
import com.bithumbsystems.persistence.mongodb.guide.service.NewsDomainService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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
    news.setIsUse(true);
    news.setReadCount(0);
    news.setCreateAccountId(account.getAccountId());
    return newsDomainService.createNews(news)
        .map(NewsMapper.INSTANCE::toDto)
        .switchIfEmpty(Mono.error(new NewsException(ErrorCode.FAIL_CREATE_CONTENT)));
  }

  /**
   * 블록체인 뉴스 목록 조회
   * @param startDate 검색 시작일자
   * @param endDate 검색 종료일자
   * @param keyword 키워드
   * @return
   */
  public Flux<NewsListResponse> getNewsList(LocalDate startDate, LocalDate endDate, String keyword) {
    return newsDomainService.findBySearchText(startDate, endDate, keyword)
        .map(news -> NewsMapper.INSTANCE.toDtoList(news, news.getAccountDocs()
            == null || news.getAccountDocs().size() < 1 ? new AdminAccount() : news.getAccountDocs().get(0)));
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

  /**
   * 블록체인 뉴스 일괄 삭제
   * @param deleteIds 게시글 ID
   * @param account 계정
   * @return
   */
  public Mono<Void> deleteNewss(String deleteIds, Account account) {
    return Flux.fromArray(deleteIds.split("::"))
        .flatMap(id -> newsDomainService.getNewsData(Long.parseLong(id))
            .flatMap(news -> {
              news.setUpdateAccountId(account.getAccountId());
              return newsDomainService.deleteNews(news);
            }))
        .then();
  }
}
