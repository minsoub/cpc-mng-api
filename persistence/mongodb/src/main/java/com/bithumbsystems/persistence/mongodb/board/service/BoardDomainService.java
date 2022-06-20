package com.bithumbsystems.persistence.mongodb.board.service;

import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import com.bithumbsystems.persistence.mongodb.board.model.entity.BoardMaster;
import com.bithumbsystems.persistence.mongodb.board.repository.BoardCustomRepository;
import com.bithumbsystems.persistence.mongodb.board.repository.BoardMasterRepository;
import com.bithumbsystems.persistence.mongodb.board.repository.BoardRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardDomainService {

  private final BoardMasterRepository boardMasterRepository;
  private final BoardRepository boardRepository;
  private final BoardCustomRepository boardCustomRepository;

  /**
   * 게시판 마스터 등록
   * @param boardMaster 게시판 마스터
   * @return
   */
  public Mono<BoardMaster> createBoardMaster(BoardMaster boardMaster) {
    boardMaster.setIsUse(true);
    boardMaster.setCreateDate(LocalDateTime.now());
    return boardMasterRepository.insert(boardMaster);
  }

  /**
   * 게시판 마스터 목록 조회
   * @return
   */
  public Flux<BoardMaster> getBoardMasters() {
    return boardMasterRepository.findAll();
  }

  /**
   * 게시판 마스터 조회
   * @param boardMasterId 게시판 ID
   * @return
   */
  public Mono<BoardMaster> getBoardMasterInfo(String boardMasterId) {
    return boardMasterRepository.findById(boardMasterId);
  }

  /**
   * 게시판 마스터 수정
   * @param boardMaster 게시판 마스터
   * @return
   */
  public Mono<BoardMaster> updateBoardMaster(BoardMaster boardMaster) {
    boardMaster.setUpdateDate(LocalDateTime.now());
    return boardMasterRepository.save(boardMaster);
  }

  /**
   * 게시판 마스터 삭제
   * @param boardMaster 게시판 마스터
   * @return
   */
  public Mono<BoardMaster> deleteBoardMaster(BoardMaster boardMaster) {
    boardMaster.setIsUse(false);
    boardMaster.setUpdateDate(LocalDateTime.now());
    return boardMasterRepository.save(boardMaster);
  }

  /**
   * 게시글 등록
   * @param board 게시글
   * @return
   */
  public Mono<Board> createBoard(Board board) {
    board.setIsUse(true);
    board.setCreateDate(LocalDateTime.now());
    return boardRepository.insert(board);
  }

  /**
   * 게시글 목록 조회
   * @param boardMasterId 게시판 ID
   * @param keyword 키워드
   * @param pageable 페이지 정보
   * @return
   */
  public Flux<Board> findPageBySearchText(String boardMasterId, String keyword, Pageable pageable) {
    return boardCustomRepository.findPageBySearchText(boardMasterId, keyword, pageable);
  }

  /**
   * 게시글 목록 건수 조회
   * @param boardMasterId 게시판 ID
   * @param keyword 키워드
   * @return
   */
  public Mono<Long> countBySearchText(String boardMasterId, String keyword) {
    return boardCustomRepository.countBySearchText(boardMasterId, keyword);
  }

  /**
   * 메인 컨텐츠 설정용 게시글 목록 조회
   * @param boardMasterId 게시판 ID
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param keyword 키워드
   * @param pageable 페이지 정보
   * @return
   */
  public Flux<Board> findPageBySearchTextForMain(String boardMasterId, LocalDate fromDate, LocalDate toDate, String keyword, Pageable pageable) {
    return boardCustomRepository.findPageBySearchTextForMain(boardMasterId, fromDate, toDate, keyword, pageable);
  }

  /**
   * 메인 컨텐츠 설정용 게시글 목록 건수 조회
   * @param boardMasterId 게시판 ID
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param keyword 키워드
   * @return
   */
  public Mono<Long> countBySearchTextForMain(String boardMasterId, LocalDate fromDate, LocalDate toDate, String keyword) {
    return boardCustomRepository.countBySearchTextForMain(boardMasterId, fromDate, toDate, keyword);
  }

  /**
   * 게시글 조회
   * @param boardId 게시글 ID
   * @return
   */
  public Mono<Board> getBoardData(Long boardId) {
    return boardRepository.findById(boardId);
  }

  /**
   * 게시글 수정
   * @param board 게시글
   * @return
   */
  public Mono<Board> updateBoard(Board board) {
    board.setUpdateDate(LocalDateTime.now());
    return boardRepository.save(board);
  }

  /**
   * 게시글 삭제
   * @param board
   * @return
   */
  public Mono<Board> deleteBoard(Board board) {
    board.setIsUse(false);
    board.setUpdateDate(LocalDateTime.now());
    return boardRepository.save(board);
  }
}
