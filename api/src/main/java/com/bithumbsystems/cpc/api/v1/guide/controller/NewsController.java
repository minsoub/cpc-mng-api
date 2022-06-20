package com.bithumbsystems.cpc.api.v1.guide.controller;

import static com.bithumbsystems.cpc.api.core.config.constant.GlobalConstant.DEFAULT_PAGE_SIZE;
import static com.bithumbsystems.cpc.api.core.config.constant.GlobalConstant.FIRST_PAGE_NUM;

import com.bithumbsystems.cpc.api.core.model.response.SingleResponse;
import com.bithumbsystems.cpc.api.v1.guide.model.request.NewsRequest;
import com.bithumbsystems.cpc.api.v1.guide.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@Tag(name = "News APIs", description = "뉴스 관련 API")
public class NewsController {

  private final NewsService newsService;

  /**
   * 블록체인 뉴스 등록
   * @param newsRequest 블록체인 뉴스
   * @return
   */
  @PostMapping
  @Operation(description = "블록체인 뉴스 등록")
  public ResponseEntity<Mono<?>> createNews(@RequestBody NewsRequest newsRequest) {
    return ResponseEntity.ok().body(newsService.createNews(newsRequest)
        .map(SingleResponse::new));
  }

  /**
   * 블록체인 뉴스 목록 조회
   * @param query 검색어
   * @return
   */
  @GetMapping
  @Operation(description = "블록체인 뉴스 목록 조회")
  public ResponseEntity<Mono<?>> getNewsList(
      @RequestParam(name = "from_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE) LocalDate fromDate,
      @RequestParam(name = "to_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE) LocalDate toDate,
      @RequestParam(name = "query", required = false, defaultValue = "") String query,
      @RequestParam(name = "page_no", defaultValue = FIRST_PAGE_NUM) int pageNo,
      @RequestParam(name = "page_size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize)
      throws UnsupportedEncodingException {
    String keyword = URLDecoder.decode(query, "UTF-8");
    log.info("keyword: {}", keyword);
    return ResponseEntity.ok().body(newsService.getNewsList(fromDate, toDate.plusDays(1), keyword, PageRequest.of(pageNo, pageSize, Sort.by("create_date").descending()))
        .map(SingleResponse::new));
  }

  /**
   * 블록체인 뉴스 조회
   * @param id ID
   * @return
   */
  @GetMapping(value = "/{id}")
  @Operation(description = "블록체인 뉴스 조회")
  public ResponseEntity<Mono<?>> getNewsData(@PathVariable Long id) {
    return ResponseEntity.ok().body(newsService.getNewsData(id)
        .map(SingleResponse::new));
  }

  /**
   * 블록체인 뉴스 수정
   * @param newsRequest 블록체인 뉴스
   * @return
   */
  @PutMapping(value = "/{id}")
  @Operation(description = "블록체인 뉴스 수정")
  public ResponseEntity<Mono<?>> updateNews(@RequestBody NewsRequest newsRequest) {
    return ResponseEntity.ok().body(newsService.updateNews(newsRequest)
        .map(SingleResponse::new));
  }

  /**
   * 블록체인 뉴스 삭제
   * @param id ID
   * @return
   */
  @DeleteMapping(value = "/{id}")
  @Operation(description = "블록체인 뉴스 삭제")
  public ResponseEntity<Mono<?>> deleteNews(@PathVariable Long id) {
    return ResponseEntity.ok().body(newsService.deleteNews(id).then(
        Mono.just(new SingleResponse()))
    );
  }
}
