package com.bithumbsystems.cpc.api.v1.main.controller;

import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.config.resolver.CurrentUser;
import com.bithumbsystems.cpc.api.core.model.response.MultiResponse;
import com.bithumbsystems.cpc.api.core.model.response.SingleResponse;
import com.bithumbsystems.cpc.api.v1.main.model.request.MainContentsRequest;
import com.bithumbsystems.cpc.api.v1.main.model.response.MainContentsResponse;
import com.bithumbsystems.cpc.api.v1.main.service.MainContentsService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
@Tag(name = "Main APIs", description = "메인 화면 관련 API")
public class MainContentsController {

  private final MainContentsService mainContentsService;

  /**
   * 메인화면 선택된 컨텐츠 조회
   * @return
   */
  @GetMapping
  @Operation(summary = "메인화면 선택된 컨텐츠 조회", description = "메인 관리 > 콘텐츠 노출 관리: 메인화면 선택된 컨텐츠 조회", tags = "메인 관리 > 콘텐츠 노출 관리")
  public ResponseEntity<Mono<?>> getMainContents() {
    return ResponseEntity.ok().body(Mono.zip(mainContentsService.getVirtualAssetBasic(),
            mainContentsService.getInsightColumn(),
            mainContentsService.getVirtualAssetTrends(),
            mainContentsService.getBlockchainNews())
        .flatMap(tuple -> Mono.just(MainContentsResponse.builder()
            .virtualAssetBasic(tuple.getT1())
            .insightColumn(tuple.getT2())
            .virtualAssetTrends(tuple.getT3())
            .blockchainNews(tuple.getT4())
            .build()))
        .map(SingleResponse::new));
  }

  /**
   * 메인 화면 컨텐츠용 게시글 목록 조회
   * @param boardMasterId 게시판 ID
   * @param fromDate 시작 일자
   * @param toDate 종료 일자
   * @param query 검색어
   * @return
   */
  @GetMapping("/{boardMasterId}")
  @Operation(summary = "메인 화면 컨텐츠용 게시글 조회", description = "메인 관리 > 콘텐츠 노출 관리: 메인 화면 컨텐츠용 게시글 조회", tags = "메인 관리 > 콘텐츠 노출 관리")
  public ResponseEntity<Mono<?>> getBoardsForMain(@PathVariable String boardMasterId,
      @RequestParam(name = "from_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE) LocalDate fromDate,
      @RequestParam(name = "to_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE) LocalDate toDate,
      @RequestParam(name = "query", required = false, defaultValue = "") String query)
      throws UnsupportedEncodingException {

    String keyword = URLDecoder.decode(query, "UTF-8");
    log.info("keyword: {}", keyword);

    return ResponseEntity.ok().body(mainContentsService.getBoardsForMain(boardMasterId, fromDate, toDate.plusDays(1), keyword)
        .collectList()
        .map(MultiResponse::new));
  }

  /**
   * 선택된 게시글 저장
   * @param mainContentsRequest 선택된 게시글 정보
   * @param account 계정
   * @return
   */
  @PostMapping
  @Operation(summary = "선택된 게시글 저장", description = "메인 관리 > 콘텐츠 노출 관리: 선택된 게시글 저장", tags = "메인 관리 > 콘텐츠 노출 관리")
  public ResponseEntity<Mono<?>> saveMainContents(@RequestBody MainContentsRequest mainContentsRequest,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(mainContentsService.saveMainContents(mainContentsRequest, account)
        .map(SingleResponse::new));
  }
}
