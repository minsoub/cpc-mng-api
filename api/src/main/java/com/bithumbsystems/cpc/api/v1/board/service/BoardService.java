package com.bithumbsystems.cpc.api.v1.board.service;

import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.model.enums.EnumMapperValue;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.v1.board.exception.BoardException;
import com.bithumbsystems.cpc.api.v1.board.mapper.BoardMapper;
import com.bithumbsystems.cpc.api.v1.board.mapper.BoardMasterMapper;
import com.bithumbsystems.cpc.api.v1.board.model.enums.BoardType;
import com.bithumbsystems.cpc.api.v1.board.model.enums.PaginationType;
import com.bithumbsystems.cpc.api.v1.board.model.request.BoardMasterRequest;
import com.bithumbsystems.cpc.api.v1.board.model.request.BoardRequest;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardListResponse;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardMasterListResponse;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardMasterResponse;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardResponse;
import com.bithumbsystems.persistence.mongodb.account.model.entity.AdminAccount;
import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import com.bithumbsystems.persistence.mongodb.board.model.entity.BoardMaster;
import com.bithumbsystems.persistence.mongodb.board.service.BoardDomainService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
   * @param account 계정
   * @return
   */
  public Mono<BoardMasterResponse> createBoardMaster(BoardMasterRequest boardMasterRequest, Account account) {
    BoardMaster boardMaster = BoardMasterMapper.INSTANCE.toEntity(boardMasterRequest, boardMasterRequest.getSnsShare(), boardMasterRequest.getAuth());
    boardMaster.setCreateAccountId(account.getAccountId());
    return boardDomainService.createBoardMaster(boardMaster)
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
   * @param account 계정
   * @return
   */
  public Mono<BoardMaster> updateBoardMaster(BoardMasterRequest boardMasterRequest, Account account) {
    BoardMaster boardMaster = BoardMasterMapper.INSTANCE.toEntity(boardMasterRequest, boardMasterRequest.getSnsShare(), boardMasterRequest.getAuth());
    boardMaster.setUpdateAccountId(account.getAccountId());
    return boardDomainService.updateBoardMaster(boardMaster)
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_UPDATE_CONTENT)));
  }

  /**
   * 게시판 마스터 삭제
   * @param boardMasterId 게시판 ID
   * @param account 계정
   * @return
   */
  public Mono<BoardMasterResponse> deleteBoardMaster(String boardMasterId, Account account) {
    return boardDomainService.getBoardMasterInfo(boardMasterId)
        .flatMap(boardMaster -> {
          boardMaster.setUpdateAccountId(account.getAccountId());
          return boardDomainService.deleteBoardMaster(boardMaster);
        })
        .map(BoardMasterMapper.INSTANCE::toDto)
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_DELETE_CONTENT)));
  }

  /**
   * 게시글 목록 조회
   * @param boardMasterId 게시판 ID
   * @param startDate 시작 일자
   * @param endDate 종료 일자
   * @param keyword 키워드
   * @return
   */
  public Flux<BoardListResponse> getBoards(String boardMasterId, LocalDate startDate, LocalDate endDate, String keyword) {
    return boardDomainService.findBySearchText(boardMasterId, startDate, endDate, keyword)
        .map(board -> BoardMapper.INSTANCE.toDtoList(board, board.getAccountDocs()
            .size() < 1 ? new AdminAccount() : board.getAccountDocs().get(0)));
  }

  /**
   * 게시글 조회
   * @param boardId 게시글 ID
   * @return
   */
  public Mono<BoardResponse> getBoardData(Long boardId) {
    return boardDomainService.getBoardData(boardId).map(BoardMapper.INSTANCE::toDto)
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.NOT_FOUND_CONTENT)));
  }

  /**
   * 게시글 등록
   * @param boardRequest 게시글
   * @param account 계정
   * @return
   */
  public Mono<BoardResponse> createBoard(BoardRequest boardRequest, Account account) {
    Board board = BoardMapper.INSTANCE.toEntity(boardRequest);
    board.setReadCount(0);
    board.setIsUse(true);
    board.setCreateAccountId(account.getAccountId());
    return boardDomainService.createBoard(board)
        .map(BoardMapper.INSTANCE::toDto)
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_CREATE_CONTENT)));
  }

  /**
   * 게시글 수정
   *
   * @param boardRequest 게시글
   * @param account 계정
   * @return
   */
  public Mono<BoardResponse> updateBoard(BoardRequest boardRequest, Account account) {
    Long boardId = boardRequest.getId();
    return boardDomainService.getBoardData(boardId)
        .flatMap(board -> {
          board.setCategory(boardRequest.getCategory());
          board.setTitle(boardRequest.getTitle());
          board.setContents(boardRequest.getContents());
          board.setIsSetNotice(boardRequest.getIsSetNotice());
          board.setTags(boardRequest.getTags());
          board.setThumbnail(boardRequest.getThumbnail());
          board.setUpdateAccountId(account.getAccountId());
          return boardDomainService.updateBoard(board);
        })
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_UPDATE_CONTENT)))
        .map(BoardMapper.INSTANCE::toDto);
  }

  /**
   * 게시글 삭제
   * @param boardId 게시글
   * @param account 계정
   * @return
   */
  public Mono<BoardResponse> deleteBoard(Long boardId, Account account) {
    return boardDomainService.getBoardData(boardId)
        .flatMap(board -> {
          board.setUpdateAccountId(account.getAccountId());
          return boardDomainService.deleteBoard(board);
        })
        .map(BoardMapper.INSTANCE::toDto)
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_DELETE_CONTENT)));
  }

  /**
   * 게시글 일괄 삭제
   * @param deleteIds 게시글 ID
   * @param account 계정
   * @return
   */
  public Mono<Void> deleteBoards(String deleteIds, Account account) {
    return Flux.fromArray(deleteIds.split("::"))
        .flatMap(boardId -> boardDomainService.getBoardData(Long.parseLong(boardId))
            .flatMap(board -> {
              board.setUpdateAccountId(account.getAccountId());
              return boardDomainService.deleteBoard(board);
            }))
        .then();
  }
}
