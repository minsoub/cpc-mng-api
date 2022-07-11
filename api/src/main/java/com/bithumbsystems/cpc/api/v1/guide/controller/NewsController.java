package com.bithumbsystems.cpc.api.v1.guide.controller;

import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.config.resolver.CurrentUser;
import com.bithumbsystems.cpc.api.core.model.response.MultiResponse;
import com.bithumbsystems.cpc.api.core.model.response.SingleResponse;
import com.bithumbsystems.cpc.api.v1.guide.model.request.NewsRequest;
import com.bithumbsystems.cpc.api.v1.guide.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

  private final NewsService newsService;

  /**
   * 블록체인 뉴스 등록
   * @param newsRequest 블록체인 뉴스
   * @param account 계정
   * @return
   */
  @PostMapping
  @Operation(summary = "블록체인 뉴스 등록", description = "콘텐츠 관리 > 블록체인 뉴스: 블록체인 뉴스 등록", tags = "콘텐츠 관리 > 블록체인 뉴스")
  public ResponseEntity<Mono<?>> createNews(@RequestBody NewsRequest newsRequest,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(newsService.createNews(newsRequest, account)
        .map(SingleResponse::new));
  }

  /**
   * 블록체인 뉴스 목록 조회
   * @param query 검색어
   * @return
   */
  @GetMapping
  @Operation(summary = "블록체인 뉴스 목록 조회", description = "콘텐츠 관리 > 블록체인 뉴스: 블록체인 뉴스 목록 조회", tags = "콘텐츠 관리 > 블록체인 뉴스")
  public ResponseEntity<Mono<?>> getNewsList(
      @RequestParam(name = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE) LocalDate startDate,
      @RequestParam(name = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE) LocalDate endDate,
      @RequestParam(name = "query", required = false, defaultValue = "") String query)
      throws UnsupportedEncodingException {
    String keyword = URLDecoder.decode(query, "UTF-8");
    log.info("keyword: {}", keyword);
    return ResponseEntity.ok().body(newsService.getNewsList(startDate, endDate.plusDays(1), keyword)
        .collectList()
        .map(MultiResponse::new));
  }

  /**
   * 블록체인 뉴스 조회
   * @param id ID
   * @return
   */
  @GetMapping(value = "/{id}")
  @Operation(summary = "블록체인 뉴스 정보 조회", description = "콘텐츠 관리 > 블록체인 뉴스: 블록체인 뉴스 정보 조회", tags = "콘텐츠 관리 > 블록체인 뉴스")
  public ResponseEntity<Mono<?>> getNewsData(@PathVariable Long id) {
    return ResponseEntity.ok().body(newsService.getNewsData(id)
        .map(SingleResponse::new));
  }

  /**
   * 블록체인 뉴스 수정
   * @param newsRequest 블록체인 뉴스
   * @param account 계정
   * @return
   */
  @PutMapping(value = "/{id}")
  @Operation(summary = "블록체인 뉴스 수정", description = "콘텐츠 관리 > 블록체인 뉴스: 블록체인 뉴스 수정", tags = "콘텐츠 관리 > 블록체인 뉴스")
  public ResponseEntity<Mono<?>> updateNews(@RequestBody NewsRequest newsRequest,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(newsService.updateNews(newsRequest, account)
        .map(SingleResponse::new));
  }

  /**
   * 블록체인 뉴스 삭제
   * @param id ID
   * @param account 계정
   * @return
   */
  @DeleteMapping(value = "/{id}")
  @Operation(summary = "블록체인 뉴스 삭제", description = "콘텐츠 관리 > 블록체인 뉴스: 블록체인 뉴스 삭제", tags = "콘텐츠 관리 > 블록체인 뉴스")
  public ResponseEntity<Mono<?>> deleteNews(@PathVariable Long id,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(newsService.deleteNews(id, account).then(
        Mono.just(new SingleResponse()))
    );
  }

  /**
   * 블록체인 뉴스 일괄 삭제
   * @param deleteIds 삭제할 id 리스트
   * @return
   */
  @DeleteMapping("/bulk-delete")
  @Operation(summary = "블록체인 뉴스 일괄 삭제", description = "콘텐츠 관리 > 블록체인 뉴스: 블록체인 뉴스 일괄 삭제", tags = "콘텐츠 관리 > 블록체인 뉴스")
  public ResponseEntity<Mono<?>> deleteBoards(@RequestParam(value = "deleteIds") String deleteIds,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(newsService.deleteNewss(deleteIds, account).then(
        Mono.just(new SingleResponse()))
    );
  }
}
