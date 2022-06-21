package com.bithumbsystems.cpc.api.v1.main.service;

import com.bithumbsystems.cpc.api.core.model.enums.EnumMapperValue;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.v1.board.mapper.BoardMapper;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardResponse;
import com.bithumbsystems.cpc.api.v1.main.exception.MainContentsException;
import com.bithumbsystems.cpc.api.v1.main.mapper.MainContentsMapper;
import com.bithumbsystems.cpc.api.v1.main.model.enums.BulletinBoardType;
import com.bithumbsystems.cpc.api.v1.main.model.request.MainContentsRequest;
import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import com.bithumbsystems.persistence.mongodb.board.service.BoardDomainService;
import com.bithumbsystems.persistence.mongodb.main.model.entity.MainContents;
import com.bithumbsystems.persistence.mongodb.main.service.MainContentsDomainService;
import java.time.LocalDate;
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
public class MainContentsService {

  private final MainContentsDomainService mainContentsDomainService;
  private final BoardDomainService boardDomainService;

  /**
   * 게시판 종류 조회
   * @return
   */
  public Flux<Object> getBulletinBoardTypes() {
    return Flux.just(Stream.of(BulletinBoardType.values())
        .map(EnumMapperValue::new)
        .collect(Collectors.toList()));
  }

  /**
   * 메인 컨텐츠 조회
   * @return
   */
  public Mono<MainContents> getMainContents() {
    return mainContentsDomainService.findOne();
  }

  /**
   * 가상 자산 동향 조회
   * @return
   */
  public Mono<List<BoardResponse>> getVirtualAssetTrends() {
    return mainContentsDomainService.findOne()
        .map(MainContents::getVirtualAssetTrends)
        .flatMapMany(it -> Flux.fromIterable(it))
        .concatMap(boardId -> boardDomainService.getBoardData(boardId).map(BoardMapper.INSTANCE::toDto))
        .collectList();
  }

  /**
   * 블록 체인 뉴스 조회
   * @return
   */
  public Mono<List<BoardResponse>> getBlockchainNews() {
    return mainContentsDomainService.findOne()
        .map(MainContents::getBlockchainNews)
        .flatMapMany(it -> Flux.fromIterable(it))
        .concatMap(boardId -> boardDomainService.getBoardData(boardId).map(BoardMapper.INSTANCE::toDto))
        .collectList();
  }

  /**
   * 투자 가이드 1 조회
   * @return
   */
  public Mono<List<BoardResponse>> getInvestmentGuide1() {
    return mainContentsDomainService.findOne()
        .map(MainContents::getInvestmentGuide1)
        .flatMapMany(it -> Flux.fromIterable(it))
        .concatMap(boardId -> boardDomainService.getBoardData(boardId).map(BoardMapper.INSTANCE::toDto))
        .collectList();
  }

  /**
   * 투자 가이드 2 조회
   * @return
   */
  public Mono<List<BoardResponse>> getInvestmentGuide2() {
    return mainContentsDomainService.findOne()
        .map(MainContents::getInvestmentGuide2)
        .flatMapMany(it -> Flux.fromIterable(it))
        .concatMap(boardId -> boardDomainService.getBoardData(boardId).map(BoardMapper.INSTANCE::toDto))
        .collectList();
  }

  /**
   * 투자 가이드 3 조회
   * @return
   */
  public Mono<List<BoardResponse>> getInvestmentGuide3() {
    return mainContentsDomainService.findOne()
        .map(MainContents::getInvestmentGuide3)
        .flatMapMany(it -> Flux.fromIterable(it))
        .concatMap(boardId -> boardDomainService.getBoardData(boardId).map(BoardMapper.INSTANCE::toDto))
        .collectList();
  }

  /**
   * 메인 화면 컨텐츠용 게시글 목록 조회
   * @param boardMasterId 게시판 ID
   * @param fromDate 시작 일자
   * @param toDate 종료 일자
   * @param keyword 키워드
   * @param pageRequest 페이지 정보
   * @return
   */
  public Mono<Page<Board>> getBoardsForMain(String boardMasterId, LocalDate fromDate, LocalDate toDate, String keyword, PageRequest pageRequest) {
    return boardDomainService.findPageBySearchTextForMain(boardMasterId, fromDate, toDate, keyword, pageRequest)
        .collectList()
        .zipWith(boardDomainService.countBySearchTextForMain(boardMasterId, fromDate, toDate, keyword)
            .map(c -> c))
        .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()));
  }

  /**
   * 메인 컨텐츠 저장
   * @param mainContentsRequest 메인 컨텐츠
   * @return
   */
  public Mono<Void> saveMainContents(MainContentsRequest mainContentsRequest) {
    return mainContentsDomainService.delete()
        .and(mainContentsDomainService.save(MainContentsMapper.INSTANCE.toEntity(mainContentsRequest)))
        .then()
        .doOnError(throwable -> Mono.error(new MainContentsException(ErrorCode.FAIL_CREATE_CONTENT)));
  }

  /**
   * 선택된 게시글 조회
   * @param classification 게시판 구분
   * @return
   */
  public Mono<List<BoardResponse>> getSelectedBoards(String classification) {
    switch (classification) {
      case "virtualAssetTrends":
        return getVirtualAssetTrends();
      case "blockchainNews":
        return getBlockchainNews();
      case "investmentGuide1":
        return getInvestmentGuide1();
      case "investmentGuide2":
        return getInvestmentGuide2();
      case "investmentGuide3":
        return getInvestmentGuide3();
    }
    return Mono.error(new MainContentsException(ErrorCode.NOT_FOUND_CONTENT));
  }
}
