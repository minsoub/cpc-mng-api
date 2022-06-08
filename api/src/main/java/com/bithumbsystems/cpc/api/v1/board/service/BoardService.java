package com.bithumbsystems.cpc.api.v1.board.service;

import com.bithumbsystems.cpc.api.core.model.enums.EnumMapperValue;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.core.util.PageSupport;
import com.bithumbsystems.cpc.api.v1.board.exception.BoardException;
import com.bithumbsystems.cpc.api.v1.board.mapper.BoardMapper;
import com.bithumbsystems.cpc.api.v1.board.mapper.BoardMasterMapper;
import com.bithumbsystems.cpc.api.v1.board.model.enums.BoardType;
import com.bithumbsystems.cpc.api.v1.board.model.enums.PaginationType;
import com.bithumbsystems.cpc.api.v1.board.model.request.BoardMasterRequest;
import com.bithumbsystems.cpc.api.v1.board.model.request.BoardRequest;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardMasterListResponse;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardMasterResponse;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardResponse;
import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import com.bithumbsystems.persistence.mongodb.board.model.entity.BoardMaster;
import com.bithumbsystems.persistence.mongodb.board.service.BoardDomainService;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardDomainService boardDomainService;

  /**
   * 게시판 유형 조회
   * @return
   */
  public Flux<Object> getBoardTypes() {
    return Flux.just(Stream.of(BoardType.values())
        .map(EnumMapperValue::new)
        .collect(Collectors.toList()));
  }

  /**
   * 페이징 유형 조회
   * @return
   */
  public Flux<Object> getPaginationTypes() {
    return Flux.just(Stream.of(PaginationType.values())
        .map(EnumMapperValue::new)
        .collect(Collectors.toList()));
  }

  /**
   * 게시판 마스터 등록
   * @param boardMasterRequest 게시판 마스터
   * @return
   */
  public Mono<BoardMasterResponse> createBoardMaster(BoardMasterRequest boardMasterRequest) {
    return boardDomainService.createBoardMaster(BoardMasterMapper.INSTANCE.toEntity(boardMasterRequest, boardMasterRequest.getCategories(), boardMasterRequest.getSnsShare(), boardMasterRequest.getAuth()))
        .map(BoardMasterMapper.INSTANCE::toDto)
        .doOnError(throwable -> Mono.error(new BoardException(ErrorCode.FAIL_CREATE_CONTENT)));
  }

  /**
   * 게시판 마스터 목록 조회
   * @return
   */
  public Flux<BoardMasterListResponse> getBoardMasters() {
    return boardDomainService.getBoardMasters().map(BoardMasterMapper.INSTANCE::toDtoForList);
  }

  /**
   * 게시판 마스터 정보 조회
   * @param boardMasterId 게시판 ID
   * @return
   */
  public Mono<BoardMasterResponse> getBoardMasterInfo(String boardMasterId) {
    return boardDomainService.getBoardMasterInfo(boardMasterId).map(BoardMasterMapper.INSTANCE::toDto);
  }

  /**
   * 게시판 마스터 수정
   * @param boardMasterRequest 게시판 마스터
   * @return
   */
  public Mono<BoardMaster> updateBoardMaster(BoardMasterRequest boardMasterRequest) {
    return boardDomainService.updateBoardMaster(BoardMasterMapper.INSTANCE.toEntity(boardMasterRequest, boardMasterRequest.getCategories(), boardMasterRequest.getSnsShare(), boardMasterRequest.getAuth()))
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_UPDATE_CONTENT)));
  }

  /**
   * 게시판 마스터 삭제
   * @param boardMasterId 게시판 ID
   * @return
   */
  public Mono<BoardMasterResponse> deleteBoardMaster(String boardMasterId) {
    return boardDomainService.getBoardMasterInfo(boardMasterId)
        .flatMap(boardDomainService::deleteBoardMaster)
        .map(BoardMasterMapper.INSTANCE::toDto)
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_DELETE_CONTENT)));
  }

  /**
   * 게시글 목록 조회
   * @param boardMasterId 게시판 ID
   * @param keyword 키워드
   * @param page
   * @return
   */
  public Mono<PageSupport<Board>> getBoards(String boardMasterId, String keyword, Pageable page) {
    return boardDomainService.getBoards(boardMasterId, keyword)
        .collectList()
        .map(list -> new PageSupport<>(
            list
                .stream()
                .sorted(Comparator
                    .comparingLong(Board::getId)
                    .reversed())
                .skip((page.getPageNumber() - 1) * page.getPageSize())
                .limit(page.getPageSize())
                .collect(Collectors.toList()),
            page.getPageNumber(), page.getPageSize(), list.size()));
  }

  /**
   * 게시글 조회
   * @param boardId 게시글 ID
   * @return
   */
  public Mono<BoardResponse> getBoardData(Long boardId) {
    return boardDomainService.getBoardData(boardId).map(BoardMapper.INSTANCE::toDto);
  }

  /**
   * 게시글 등록
   * @param boardRequest 게시글
   * @return
   */
  public Mono<BoardResponse> createBoard(BoardRequest boardRequest) {
    return boardDomainService.createBoard(BoardMapper.INSTANCE.toEntity(boardRequest))
        .map(BoardMapper.INSTANCE::toDto)
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_CREATE_CONTENT)));
  }

  /**
   * 게시글 수정
   *
   * @param boardRequest 게시글
   * @return
   */
  public Mono<Board> updateBoard(BoardRequest boardRequest) {
    Long boardId = boardRequest.getId();
    return boardDomainService.getBoardData(boardId)
        .flatMap(board -> {
          board.setTitle(boardRequest.getTitle());
          board.setContents(boardRequest.getContents());
          return boardDomainService.updateBoard(board);
        })
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_UPDATE_CONTENT)));
  }

  /**
   * 게시글 삭제
   * @param boardId 게시글
   * @return
   */
  public Mono<BoardResponse> deleteBoard(Long boardId) {
    return boardDomainService.getBoardData(boardId)
        .flatMap(boardDomainService::deleteBoard)
        .map(BoardMapper.INSTANCE::toDto)
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_DELETE_CONTENT)));
  }
}
