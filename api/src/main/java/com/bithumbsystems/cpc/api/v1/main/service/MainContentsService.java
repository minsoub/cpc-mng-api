package com.bithumbsystems.cpc.api.v1.main.service;

import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.v1.board.mapper.BoardMapper;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardListResponse;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardResponse;
import com.bithumbsystems.cpc.api.v1.guide.mapper.NewsMapper;
import com.bithumbsystems.cpc.api.v1.guide.model.response.NewsResponse;
import com.bithumbsystems.cpc.api.v1.main.exception.MainContentsException;
import com.bithumbsystems.cpc.api.v1.main.mapper.MainContentsMapper;
import com.bithumbsystems.cpc.api.v1.main.model.request.MainContentsRequest;
import com.bithumbsystems.cpc.api.v1.main.model.response.SelectedBoardResponse;
import com.bithumbsystems.persistence.mongodb.account.model.entity.AdminAccount;
import com.bithumbsystems.persistence.mongodb.board.service.BoardDomainService;
import com.bithumbsystems.persistence.mongodb.guide.service.NewsDomainService;
import com.bithumbsystems.persistence.mongodb.main.model.entity.MainContents;
import com.bithumbsystems.persistence.mongodb.main.service.MainContentsDomainService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  public Mono<List<BoardResponse>> getDigitalAssetBasic() {
    return mainContentsDomainService.findOne()
        .map(MainContents::getDigitalAssetBasic)
        .flatMapMany(it -> Flux.fromIterable(it))
        .concatMap(boardId -> boardDomainService.getBoardData(boardId)
            .map(board -> BoardMapper.INSTANCE.toDto(board, board.getAccountDocs()
                == null || board.getAccountDocs().size() < 1 ? new AdminAccount() : board.getAccountDocs().get(0))))
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
        .concatMap(boardId -> boardDomainService.getBoardData(boardId)
            .map(board -> BoardMapper.INSTANCE.toDto(board, board.getAccountDocs()
                == null || board.getAccountDocs().size() < 1 ? new AdminAccount() : board.getAccountDocs().get(0))))
        .collectList();
  }

  /**
   * 가상 자산 동향 조회
   * @return
   */
  public Mono<List<BoardResponse>> getDigitalAssetTrends() {
    return mainContentsDomainService.findOne()
        .map(MainContents::getDigitalAssetTrends)
        .flatMapMany(it -> Flux.fromIterable(it))
        .concatMap(boardId -> boardDomainService.getBoardData(boardId)
            .map(board -> BoardMapper.INSTANCE.toDto(board, board.getAccountDocs()
                == null || board.getAccountDocs().size() < 1 ? new AdminAccount() : board.getAccountDocs().get(0))))
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
   * @param startDate 시작 일자
   * @param endDate 종료 일자
   * @param keyword 키워드
   * @return
   */
  public Flux<SelectedBoardResponse> getBoardsForMain(String boardMasterId, LocalDate startDate, LocalDate endDate, String keyword) {
    if ("CPC_NEWS".equals(boardMasterId)) {
      return newsDomainService.findBySearchText(startDate, endDate, keyword)
          .map(news -> SelectedBoardResponse.builder()
                  .boardMasterId("CPC_NEWS")
                  .id(news.getId())
                  .title(news.getTitle())
                  .createDate(news.getCreateDate())
                  .build()
          );
    }
    else {
      return boardDomainService.findBySearchTextForMain(boardMasterId, startDate, endDate, keyword)
          .map(board -> SelectedBoardResponse.builder()
              .boardMasterId(board.getBoardMasterId())
              .id(board.getId())
              .title(board.getTitle())
              .createDate(board.getCreateDate())
              .build()
          );
    }
  }

  /**
   * 메인 컨텐츠 저장
   * @param mainContentsRequest 메인 컨텐츠
   * @param account 계정
   * @return
   */
  public Mono<Void> saveMainContents(MainContentsRequest mainContentsRequest, Account account) {
    MainContents mainContents = MainContentsMapper.INSTANCE.toEntity(mainContentsRequest);
    mainContents.setCreateAccountId(account.getAccountId());
    mainContents.setUpdateAccountId(account.getAccountId());
    return mainContentsDomainService.delete()
        .and(mainContentsDomainService.save(mainContents))
        .then()
        .doOnError(throwable -> Mono.error(new MainContentsException(ErrorCode.FAIL_CREATE_CONTENT)));
  }
}
