package com.bithumbsystems.cpc.api.v1.main.service;

import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.v1.board.mapper.BoardMapper;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardResponse;
import com.bithumbsystems.cpc.api.v1.guide.mapper.NewsMapper;
import com.bithumbsystems.cpc.api.v1.guide.model.response.NewsResponse;
import com.bithumbsystems.cpc.api.v1.main.exception.MainContentsException;
import com.bithumbsystems.cpc.api.v1.main.mapper.MainContentsMapper;
import com.bithumbsystems.cpc.api.v1.main.model.request.MainContentsRequest;
import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import com.bithumbsystems.persistence.mongodb.board.service.BoardDomainService;
import com.bithumbsystems.persistence.mongodb.guide.service.NewsDomainService;
import com.bithumbsystems.persistence.mongodb.main.model.entity.MainContents;
import com.bithumbsystems.persistence.mongodb.main.service.MainContentsDomainService;
import java.time.LocalDate;
import java.util.List;
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
  private final NewsDomainService newsDomainService;

  /**
   * 가상 자산 기초 조회
   * @return
   */
  public Mono<List<BoardResponse>> getVirtualAssetBasic() {
    return mainContentsDomainService.findOne()
        .map(MainContents::getVirtualAssetBasic)
        .flatMapMany(it -> Flux.fromIterable(it))
        .concatMap(boardId -> boardDomainService.getBoardData(boardId).map(BoardMapper.INSTANCE::toDto))
        .collectList();
  }

  /**
   * 인사이트 칼럼 조회
   * @return
   */
  public Mono<List<BoardResponse>> getInsightColumn() {
    return mainContentsDomainService.findOne()
        .map(MainContents::getInsightColumn)
        .flatMapMany(it -> Flux.fromIterable(it))
        .concatMap(boardId -> boardDomainService.getBoardData(boardId).map(BoardMapper.INSTANCE::toDto))
        .collectList();
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
  public Mono<List<NewsResponse>> getBlockchainNews() {
    return mainContentsDomainService.findOne()
        .map(MainContents::getBlockchainNews)
        .flatMapMany(it -> Flux.fromIterable(it))
        .concatMap(boardId -> newsDomainService.getNewsData(boardId).map(NewsMapper.INSTANCE::toDto))
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

//  /**
//   * 선택된 게시글 조회
//   * @param classification 게시판 구분
//   * @return
//   */
//  public Mono<List<BoardResponse>> getSelectedBoards(String classification) {
//    switch (classification) {
//      case "virtualAssetTrends":
//        return getVirtualAssetTrends();
//      case "blockchainNews":
//        return getBlockchainNews();
//      case "investmentGuide1":
//        return getInvestmentGuide1();
//      case "investmentGuide2":
//        return getInvestmentGuide2();
//      case "investmentGuide3":
//        return getInvestmentGuide3();
//    }
//    return Mono.error(new MainContentsException(ErrorCode.NOT_FOUND_CONTENT));
//  }
}
