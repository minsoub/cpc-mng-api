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
    board.setCreateDate(LocalDateTime.now());
    return boardRepository.insert(board);
  }

  /**
   * 게시글 목록 조회
   * @param boardMasterId 게시판 ID
   * @param startDate 시작 일자
   * @param endDate 종료 일자
   * @param keyword 키워드
   * @param category 카테고리
   * @return
   */
  public Flux<Board> findBySearchText(String boardMasterId, LocalDate startDate, LocalDate endDate, String keyword, String category) {
    return boardCustomRepository.findBySearchText(boardMasterId, startDate, endDate, keyword, category);
  }

  /**
   * 메인 컨텐츠 설정용 게시글 목록 조회
   * @param boardMasterId 게시판 ID
   * @param startDate 검색 시작일자
   * @param endDate 검색 종료일자
   * @param keyword 키워드
   * @return
   */
  public Flux<Board> findBySearchTextForMain(String boardMasterId, LocalDate startDate, LocalDate endDate, String keyword) {
    return boardCustomRepository.findBySearchTextForMain(boardMasterId, startDate, endDate, keyword);
  }

  /**
   * 게시글 조회
   * @param boardId 게시글 ID
   * @return
   */
  public Mono<Board> getBoardData(Long boardId) {
    return boardCustomRepository.findById(boardId).next();
  }

  /**
   * 게시글 수정
   * @param board 게시글
   * @return
   */
  public Mono<Board> updateBoard(Board board) {
    board.setUpdateDate(LocalDateTime.now());
    board.setAccountDocs(null);
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
    board.setAccountDocs(null);
    return boardRepository.save(board);
  }
}
