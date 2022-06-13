package com.bithumbsystems.cpc.api.v1.main.controller;

import static com.bithumbsystems.cpc.api.core.util.PageSupport.DEFAULT_PAGE_SIZE;
import static com.bithumbsystems.cpc.api.core.util.PageSupport.FIRST_PAGE_NUM;

import com.bithumbsystems.cpc.api.core.model.response.MultiResponse;
import com.bithumbsystems.cpc.api.core.model.response.SingleResponse;
import com.bithumbsystems.cpc.api.v1.main.model.request.MainContentsRequest;
import com.bithumbsystems.cpc.api.v1.main.model.response.MainContentsResponse;
import com.bithumbsystems.cpc.api.v1.main.service.MainContentsService;
import com.bithumbsystems.persistence.mongodb.main.model.entity.MainContents;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
   * 게시판 종류 조회
   * @return
   */
  @GetMapping(value = "/bulletin-board-types")
  @Operation(description = "게시판 종류 조회")
  public ResponseEntity<Mono<?>> getBulletinBoardTypes() {
    return ResponseEntity.ok().body(mainContentsService.getBulletinBoardTypes()
        .collectList()
        .map(list -> new MultiResponse(list)));
  }

  /**
   * 메인화면 선택된 컨텐츠 조회
   * @return
   */
  @GetMapping
  @Operation(description = "메인화면 선택된 컨텐츠 조회")
  public ResponseEntity<Mono<?>> getMainContents() {
    return ResponseEntity.ok().body(Mono.zip(mainContentsService.getMainContents(),
            mainContentsService.getVirtualAssetTrends(),
            mainContentsService.getBlockchainNews(),
            mainContentsService.getInvestmentGuide1(),
            mainContentsService.getInvestmentGuide2(),
            mainContentsService.getInvestmentGuide3())
        .flatMap(tuple -> Mono.just(MainContentsResponse.builder()
            .virtualAssetTrends(tuple.getT2())
            .blockchainNews(tuple.getT3())
            .investmentGuide1Id(tuple.getT1().getInvestmentGuide1Id())
            .investmentGuide1(tuple.getT4())
            .investmentGuide2Id(tuple.getT1().getInvestmentGuide2Id())
            .investmentGuide2(tuple.getT5())
            .investmentGuide3Id(tuple.getT1().getInvestmentGuide3Id())
            .investmentGuide3(tuple.getT6())
            .build()))
        .map(response -> new SingleResponse(response)));
  }

  /**
   * 메인 화면 컨텐츠용 게시글 목록 조회
   * @param boardMasterId 게시판 ID
   * @param fromDate 시작 일자
   * @param toDate 종료 일자
   * @param query 검색어
   * @param pageNo - 페이지 번호
   * @param pageSize - 페이지 사이즈
   * @return
   */
  @GetMapping("/{boardMasterId}")
  @Operation(description = "메인 화면 컨텐츠용 게시글 조회")
  public ResponseEntity<Mono<?>> getBoardsForMain(@PathVariable String boardMasterId,
      @RequestParam(name = "fromDate") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime fromDate,
      @RequestParam(name = "toDate") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime toDate,
      @RequestParam(name = "query", required = false, defaultValue = "") String query,
      @RequestParam(name = "pageNo", defaultValue = FIRST_PAGE_NUM) int pageNo,
      @RequestParam(name = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize)
      throws UnsupportedEncodingException {

    String keyword = URLDecoder.decode(query, "UTF-8");
    log.info("keyword: {}", keyword);

    return ResponseEntity.ok().body(mainContentsService.getBoardsForMain(boardMasterId, fromDate, toDate, keyword, PageRequest.of(pageNo, pageSize))
        .map(response -> new SingleResponse(response)));
  }

  /**
   * 선택된 게시글 저장
   * @param mainContentsRequest 선택된 게시글 정보
   * @return
   */
  @PostMapping
  @Operation(description = "선택된 게시글 저장")
  public ResponseEntity<Mono<?>> saveMainContents(@RequestBody MainContentsRequest mainContentsRequest) {
    return ResponseEntity.ok().body(mainContentsService.saveMainContents(mainContentsRequest)
        .map(response -> new SingleResponse(response)));
  }

  /**
   * 선택된 게시글 조회
   * @param classification 게시판 구분
   * @return
   */
  @GetMapping("/selected")
  @Operation(description = "선택된 게시글 조회")
  public ResponseEntity<Mono<?>> getSelectedBoards(@RequestParam(value = "classification") String classification) {
    return ResponseEntity.ok().body(mainContentsService.getSelectedBoards(classification)
        .map(list -> new MultiResponse(list))
    );
  }
}
