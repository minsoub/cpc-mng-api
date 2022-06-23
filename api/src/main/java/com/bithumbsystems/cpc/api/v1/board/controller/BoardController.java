package com.bithumbsystems.cpc.api.v1.board.controller;

import static com.bithumbsystems.cpc.api.core.config.constant.GlobalConstant.DEFAULT_PAGE_SIZE;
import static com.bithumbsystems.cpc.api.core.config.constant.GlobalConstant.FIRST_PAGE_NUM;

import com.bithumbsystems.cpc.api.core.model.response.MultiResponse;
import com.bithumbsystems.cpc.api.core.model.response.SingleResponse;
import com.bithumbsystems.cpc.api.v1.board.model.request.BoardMasterRequest;
import com.bithumbsystems.cpc.api.v1.board.model.request.BoardRequest;
import com.bithumbsystems.cpc.api.v1.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@Tag(name = "Board APIs", description = "게시판 관련 API")
public class BoardController {
  private final BoardService boardService;

  /**
   * 게시판 유형 조회
   * @return
   */
  @GetMapping(value = "/board-types")
  @Operation(description = "게시판 유형 조회")
  public ResponseEntity<Mono<?>> getBoardTypes() {
    return ResponseEntity.ok().body(boardService.getBoardTypes()
        .collectList()
        .map(MultiResponse::new));
  }

  /**
   * 페이징 유형 조회
   * @return
   */
  @GetMapping(value = "/pagination-types")
  @Operation(description = "페이징 유형 조회")
  public ResponseEntity<Mono<?>> getPaginationTypes() {
    return ResponseEntity.ok().body(boardService.getPaginationTypes()
        .collectList()
        .map(MultiResponse::new));
  }

  /**
   * 게시판 마스터 등록
   * @return
   */
  @PostMapping
  @Operation(description = "게시판 마스터 등록")
  public ResponseEntity<Mono<?>> createBoardMaster(@RequestBody BoardMasterRequest boardMasterRequest) {
    return ResponseEntity.ok().body(boardService.createBoardMaster(boardMasterRequest)
        .map(SingleResponse::new));
  }

  /**
   * 게시판 마스터 목록 조회
   * @return
   */
  @GetMapping
  @Operation(description = "게시판 마스터 목록 조회")
  public ResponseEntity<Mono<?>> getBoards() {
    return ResponseEntity.ok().body(boardService.getBoardMasters()
        .collectList()
        .map(MultiResponse::new));
  }

  /**
   * 게시판 마스터 정보 조회
   * @param boardMasterId 게시판 ID
   * @param siteId 싸이트 ID
   * @return
   */
  @GetMapping("/{boardMasterId}/info")
  @Operation(description = "게시판 마스터 정보 조회")
  public ResponseEntity<Mono<?>> getBoardMasterInfo(@PathVariable String boardMasterId, @RequestHeader(value = "site_id") String siteId) {
    return ResponseEntity.ok().body(boardService.getBoardMasterInfo(boardMasterId, siteId)
        .map(SingleResponse::new));
  }

  /**
   * 게시판 마스터 수정
   * @param boardMasterId 게시판 ID
   * @param boardMasterRequest 게시판 정보
   * @return
   */
  @PutMapping("/{boardMasterId}")
  @Operation(description = "게시판 마스터 수정")
  public ResponseEntity<Mono<?>> updateBoardMaster(@PathVariable String boardMasterId, @RequestBody BoardMasterRequest boardMasterRequest) {
    return ResponseEntity.ok().body(boardService.updateBoardMaster(boardMasterRequest)
        .map(SingleResponse::new));
  }

  /**
   * 게시판 마스터 삭제
   * @param boardMasterId 게시판 ID
   * @return
   */
  @DeleteMapping("/{boardMasterId}")
  @Operation(description = "게시판 마스터 삭제")
  public ResponseEntity<Mono<?>> deleteBoardMaster(@PathVariable String boardMasterId, @RequestHeader(value = "site_id") String siteId) {
    return ResponseEntity.ok().body(boardService.deleteBoardMaster(boardMasterId, siteId).then(
        Mono.just(new SingleResponse()))
    );
  }

  /**
   * 게시글 목록 조회
   * @param boardMasterId 게시판 ID
   * @param query 검색어
   * @param pageNo - 페이지 번호
   * @param pageSize - 페이지 사이즈
   * @return
   */
  @GetMapping("/{boardMasterId}")
  @Operation(description = "게시글 목록 조회")
  public ResponseEntity<Mono<?>> getBoards(
      @PathVariable String boardMasterId,
      @RequestParam(name = "query", required = false, defaultValue = "") String query,
      @RequestParam(name = "page_no", defaultValue = FIRST_PAGE_NUM) int pageNo,
      @RequestParam(name = "page_size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize)
      throws UnsupportedEncodingException {
    String keyword = URLDecoder.decode(query, "UTF-8");
    log.info("keyword: {}", keyword);

    return ResponseEntity.ok().body(boardService.getBoards(boardMasterId, keyword, PageRequest.of(pageNo, pageSize, Sort.by("create_date").descending()))
        .map(SingleResponse::new));
  }

  /**
   * 게시글 조회
   * @param boardMasterId 게시판 ID
   * @param boardId 게시글 ID
   * @return
   */
  @GetMapping("/{boardMasterId}/{boardId}")
  @Operation(description = "게시글 조회")
  public ResponseEntity<Mono<?>> getBoardData(@PathVariable String boardMasterId, @PathVariable Long boardId) {
    return ResponseEntity.ok().body(boardService.getBoardData(boardId)
        .map(SingleResponse::new));
  }

  /**
   * 게시글 등록
   * @param boardMasterId 게시판 ID
   * @param boardRequest 게시글
   * @return
   */
  @PostMapping("/{boardMasterId}")
  @Operation(description = "게시글 등록")
  public ResponseEntity<Mono<?>> createBoard(@PathVariable String boardMasterId, @RequestBody BoardRequest boardRequest) {
    boardRequest.setBoardMasterId(boardMasterId);
    return ResponseEntity.ok().body(boardService.createBoard(boardRequest)
        .map(SingleResponse::new));
  }

  /**
   * 게시글 수정
   * @param boardMasterId 게시판 ID
   * @param boardRequest 게시글
   * @return
   */
  @PutMapping("/{boardMasterId}/{boardId}")
  @Operation(description = "게시글 수정")
  public ResponseEntity<Mono<?>> updateBoard(@PathVariable String boardMasterId, @RequestBody BoardRequest boardRequest) {
    return ResponseEntity.ok().body(boardService.updateBoard(boardRequest)
        .map(SingleResponse::new));
  }

  /**
   * 게시글 삭제
   * @param boardMasterId 게시판 ID
   * @param boardId 게시글 ID
   * @return
   */
  @DeleteMapping("/{boardMasterId}/{boardId}")
  @Operation(description = "게시글 삭제")
  public ResponseEntity<Mono<?>> deleteBoard(@PathVariable String boardMasterId, @PathVariable Long boardId) {
    return ResponseEntity.ok().body(boardService.deleteBoard(boardId).then(
        Mono.just(new SingleResponse()))
    );
  }
}
