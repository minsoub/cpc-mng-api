package com.bithumbsystems.cpc.api.v1.board.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.config.resolver.CurrentUser;
import com.bithumbsystems.cpc.api.core.model.response.MultiResponse;
import com.bithumbsystems.cpc.api.core.model.response.SingleResponse;
import com.bithumbsystems.cpc.api.v1.board.model.request.BoardMasterRequest;
import com.bithumbsystems.cpc.api.v1.board.model.request.BoardRequest;
import com.bithumbsystems.cpc.api.v1.board.service.BoardService;
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
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
  private final BoardService boardService;

  /**
   * 게시판 유형 조회
   * @return
   */
  @GetMapping(value = "/board-types")
  @Operation(summary = "게시판 유형 조회", description = "게시판 유형 조회", tags = "게시판 화면 공통")
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
  @Operation(summary = "페이징 유형 조회", description = "게시판 유형 조회", tags = "게시판 화면 공통")
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
  @Operation(summary = "게시판 마스터 등록", description = "통합관리 > 통합게시판 관리 > 게시판 생성: 게시판 마스터 등록", tags = "통합관리 > 통합게시판 관리 > 게시판 관리")
  public ResponseEntity<Mono<?>> createBoardMaster(@RequestBody BoardMasterRequest boardMasterRequest,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(boardService.createBoardMaster(boardMasterRequest, account)
        .map(SingleResponse::new));
  }

  /**
   * 게시판 마스터 목록 조회
   * @return
   */
  @GetMapping
  @Operation(summary = "게시판 마스터 목록 조회", description = "통합관리 > 통합게시판 관리 > 게시판 관리: 게시판 마스터 등록", tags = "통합관리 > 통합게시판 관리 > 게시판 관리")
  public ResponseEntity<Mono<?>> getBoards() {
    return ResponseEntity.ok().body(boardService.getBoardMasters()
        .collectList()
        .map(MultiResponse::new));
  }

  /**
   * 게시판 마스터 정보 조회
   * @param boardMasterId 게시판 ID
   * @return
   */
  @GetMapping("/{boardMasterId}/info")
  @Operation(summary = "게시판 마스터 정보 조회", description = "게시판 설정 정보를 가진 마스터 정보를 조회", tags = "게시판 화면 공통")
  public ResponseEntity<Mono<?>> getBoardMasterInfo(@PathVariable String boardMasterId) {
    return ResponseEntity.ok().body(boardService.getBoardMasterInfo(boardMasterId)
        .map(SingleResponse::new));
  }

  /**
   * 게시판 마스터 수정
   * @param boardMasterId 게시판 ID
   * @param boardMasterRequest 게시판 정보
   * @return
   */
  @PutMapping("/{boardMasterId}")
  @Operation(summary = "게시판 마스터 수정", description = "통합관리 > 통합게시판 관리 > 게시판 관리: 게시판 마스터 수정", tags = "통합관리 > 통합게시판 관리 > 게시판 관리")
  public ResponseEntity<Mono<?>> updateBoardMaster(@PathVariable String boardMasterId,
      @RequestBody BoardMasterRequest boardMasterRequest,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(boardService.updateBoardMaster(boardMasterRequest, account)
        .map(SingleResponse::new));
  }

  /**
   * 게시판 마스터 삭제
   * @param boardMasterId 게시판 ID
   * @return
   */
  @DeleteMapping("/{boardMasterId}")
  @Operation(summary = "게시판 마스터 삭제", description = "통합관리 > 통합게시판 관리 > 게시판 관리: 게시판 마스터 삭제", tags = "통합관리 > 통합게시판 관리 > 게시판 관리")
  public ResponseEntity<Mono<?>> deleteBoardMaster(@PathVariable String boardMasterId,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(boardService.deleteBoardMaster(boardMasterId, account).then(
        Mono.just(new SingleResponse()))
    );
  }

  /**
   * 게시글 목록 조회
   * @param boardMasterId 게시판 ID
   * @param startDate 검색 시작일자
   * @param endDate 검색 종료일자
   * @param query 검색어
   * @param category 카테고리
   * @return
   */
  @GetMapping("/{boardMasterId}")
  @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 페이지 단위로 조회", tags = "게시판 화면 공통")
  public ResponseEntity<Mono<?>> getBoards(
      @PathVariable String boardMasterId,
      @RequestParam(name = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE) LocalDate startDate,
      @RequestParam(name = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE) LocalDate endDate,
      @RequestParam(name = "query", required = false, defaultValue = "") String query,
      @RequestParam(name = "category", required = false, defaultValue = "") String category)
      throws UnsupportedEncodingException {
    String keyword = URLDecoder.decode(query, "UTF-8");
    log.info("keyword: {}", keyword);
    String decodingCategory = URLDecoder.decode(category, "UTF-8");
    log.info("category: {}", decodingCategory);

    return ResponseEntity.ok().body(boardService.getBoards(boardMasterId, startDate, endDate.plusDays(1), keyword, decodingCategory)
        .collectList()
        .map(MultiResponse::new));
  }

  /**
   * 게시글 조회
   * @param boardMasterId 게시판 ID
   * @param boardId 게시글 ID
   * @return
   */
  @GetMapping("/{boardMasterId}/{boardId}")
  @Operation(summary = "게시글 조회", description = "게시글 정보를 조회", tags = "게시판 화면 공통")
  public ResponseEntity<Mono<?>> getBoardData(@PathVariable String boardMasterId, @PathVariable Long boardId) {
    return ResponseEntity.ok().body(boardService.getBoardData(boardId)
        .map(SingleResponse::new));
  }

  /**
   * 게시글 등록
   * @param boardMasterId 게시판 ID
   * @param filePart 썸네일이미지 파일
   * @param boardRequest 게시글
   * @return
   */
  @PostMapping(value = "/{boardMasterId}", consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
  @Operation(summary = "게시글 등록", description = "게시글 정보를 등록", tags = "게시판 화면 공통")
  public ResponseEntity<Mono<?>> createBoard(@PathVariable String boardMasterId,
      @RequestPart(value = "boardRequest") BoardRequest boardRequest,
      @RequestPart(value = "file", required = false) FilePart filePart,
      @Parameter(hidden = true) @CurrentUser Account account) {
    boardRequest.setBoardMasterId(boardMasterId);
    return ResponseEntity.ok().body(boardService.createBoard(filePart, boardRequest, account)
        .map(SingleResponse::new));
  }

  /**
   * 게시글 수정
   * @param boardMasterId 게시판 ID
   * @param filePart 썸네일이미지 파일
   * @param boardRequest 게시글
   * @return
   */
  @PutMapping(value = "/{boardMasterId}/{boardId}", consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
  @Operation(summary = "게시글 수정", description = "게시글 정보를 수정", tags = "게시판 화면 공통")
  public ResponseEntity<Mono<?>> updateBoard(@PathVariable String boardMasterId,
      @PathVariable String boardId,
      @RequestPart(value = "boardRequest") BoardRequest boardRequest,
      @RequestPart(value = "file", required = false) FilePart filePart,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(boardService.updateBoard(filePart, boardRequest, account)
        .map(SingleResponse::new));
  }

  /**
   * 게시글 삭제
   * @param boardMasterId 게시판 ID
   * @param boardId 게시글 ID
   * @return
   */
  @DeleteMapping("/{boardMasterId}/{boardId}")
  @Operation(summary = "게시글 삭제", description = "게시글 정보를 삭제", tags = "게시판 화면 공통")
  public ResponseEntity<Mono<?>> deleteBoard(@PathVariable String boardMasterId,
      @PathVariable Long boardId,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(boardService.deleteBoard(boardId, account).then(
        Mono.just(new SingleResponse()))
    );
  }

  /**
   * 게시글 일괄 삭제
   * @param boardMasterId 게시판 ID
   * @param deleteIds 삭제할 id 리스트
   * @return
   */
  @DeleteMapping("/{boardMasterId}/bulk-delete")
  @Operation(summary = "게시글 일괄 삭제", description = "게시글 정보를 일괄 삭제", tags = "게시판 화면 공통")
  public ResponseEntity<Mono<?>> deleteBoards(@PathVariable String boardMasterId,
      @RequestParam(value = "deleteIds") String deleteIds,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(boardService.deleteBoards(deleteIds, account).then(
        Mono.just(new SingleResponse()))
    );
  }

  /**
   * 이미지 업로드
   * @param filePart 이미지 파일
   * @return
   */
  @PostMapping(value = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "이미지 업로드", description = "AWS S3에 이미지 업로드", tags = "게시판 화면 공통")
  public ResponseEntity<Mono<?>> uploadImage(@RequestPart(value = "files[0]", required = false) FilePart filePart,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(boardService.uploadImage(filePart, account)
        .map(SingleResponse::new));
  }
}
