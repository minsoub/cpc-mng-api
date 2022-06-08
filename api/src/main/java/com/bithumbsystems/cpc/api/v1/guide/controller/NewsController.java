package com.bithumbsystems.cpc.api.v1.guide.controller;

import com.bithumbsystems.cpc.api.core.model.response.MultiResponse;
import com.bithumbsystems.cpc.api.v1.guide.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
   * 뉴스 목록 조회
   * @param query 검색어
   * @return
   */
  @GetMapping
  @Operation(description = "뉴스 목록 조회")
  public ResponseEntity<Mono<?>> getBoardDataList(@RequestParam(name = "query", required = false, defaultValue = "") String query)
      throws UnsupportedEncodingException {
    String keyword = URLDecoder.decode(query, "UTF-8");
    log.info("keyword: {}", keyword);
    return ResponseEntity.ok().body(newsService.getNewsList(keyword)
        .collectList()
        .map(newsResponseList -> new MultiResponse(newsResponseList)));
  }
}
