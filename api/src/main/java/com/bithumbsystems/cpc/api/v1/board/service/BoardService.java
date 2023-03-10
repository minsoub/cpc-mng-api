package com.bithumbsystems.cpc.api.v1.board.service;

import com.bithumbsystems.cpc.api.core.config.property.AwsProperties;
import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.model.enums.EnumMapperValue;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.core.util.FileUtil;
import com.bithumbsystems.cpc.api.core.util.ValidationUtils;
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
import com.bithumbsystems.cpc.api.v1.board.model.response.UploaderData;
import com.bithumbsystems.cpc.api.v1.board.model.response.UploaderDataInfo;
import com.bithumbsystems.persistence.mongodb.account.model.entity.AdminAccount;
import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import com.bithumbsystems.persistence.mongodb.board.model.entity.BoardMaster;
import com.bithumbsystems.persistence.mongodb.board.service.BoardDomainService;
import com.bithumbsystems.persistence.mongodb.common.model.entity.File;
import com.bithumbsystems.persistence.mongodb.common.service.FileDomainService;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardDomainService boardDomainService;
  private final AwsProperties awsProperties;
  private final S3AsyncClient s3AsyncClient;
  private final FileDomainService fileDomainService;

  @Value("${webserver.board-url}")
  private String boardBucketUrl;

  /**
   * ????????? ?????? ??????
   * @return
   */
  public Flux<Object> getBoardTypes() {
    return Flux.just(Stream.of(BoardType.values())
        .map(EnumMapperValue::new)
        .collect(Collectors.toList()));
  }

  /**
   * ????????? ?????? ??????
   * @return
   */
  public Flux<Object> getPaginationTypes() {
    return Flux.just(Stream.of(PaginationType.values())
        .map(EnumMapperValue::new)
        .collect(Collectors.toList()));
  }

  /**
   * ????????? ????????? ??????
   * @param boardMasterRequest ????????? ?????????
   * @param account ??????
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
   * ????????? ????????? ?????? ??????
   * @return
   */
  public Flux<BoardMasterListResponse> getBoardMasters() {
    return boardDomainService.getBoardMasters().map(BoardMasterMapper.INSTANCE::toDtoForList);
  }

  /**
   * ????????? ????????? ?????? ??????
   * @param boardMasterId ????????? ID
   * @return
   */
  public Mono<BoardMasterResponse> getBoardMasterInfo(String boardMasterId) {
    return boardDomainService.getBoardMasterInfo(boardMasterId).map(BoardMasterMapper.INSTANCE::toDto);
  }

  /**
   * ????????? ????????? ??????
   * @param boardMasterRequest ????????? ?????????
   * @param account ??????
   * @return
   */
  public Mono<BoardMaster> updateBoardMaster(BoardMasterRequest boardMasterRequest, Account account) {
    BoardMaster boardMaster = BoardMasterMapper.INSTANCE.toEntity(boardMasterRequest, boardMasterRequest.getSnsShare(), boardMasterRequest.getAuth());
    boardMaster.setUpdateAccountId(account.getAccountId());
    return boardDomainService.updateBoardMaster(boardMaster)
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_UPDATE_CONTENT)));
  }

  /**
   * ????????? ????????? ??????
   * @param boardMasterId ????????? ID
   * @param account ??????
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
   * ????????? ?????? ??????
   * @param boardMasterId ????????? ID
   * @param startDate ?????? ??????
   * @param endDate ?????? ??????
   * @param keyword ?????????
   * @param category ????????????
   * @return
   */
  public Flux<BoardListResponse> getBoards(String boardMasterId, LocalDate startDate, LocalDate endDate, String keyword, String category) {
    return boardDomainService.findBySearchText(boardMasterId, startDate, endDate, keyword, category)
        .map(board -> BoardMapper.INSTANCE.toDtoList(board, board.getAccountDocs()
            == null || board.getAccountDocs().size() < 1 ? new AdminAccount() : board.getAccountDocs().get(0)));
  }

  /**
   * ????????? ??????
   * @param boardId ????????? ID
   * @return
   */
  public Mono<BoardResponse> getBoardData(Long boardId) {
    return boardDomainService.getBoardData(boardId)
        .map(board -> BoardMapper.INSTANCE.toDto(board, board.getAccountDocs()
            == null || board.getAccountDocs().size() < 1 ? new AdminAccount() : board.getAccountDocs().get(0)))
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.NOT_FOUND_CONTENT)));
  }

  /**
   * ????????? ??????
   * @param filePart ?????????????????? ??????
   * @param boardRequest ?????????
   * @param account ??????
   * @return
   */
  public Mono<BoardResponse> createBoard(FilePart filePart, BoardRequest boardRequest, Account account) {
    Board board = BoardMapper.INSTANCE.toEntity(boardRequest);
    board.setReadCount(0);
    board.setIsUse(true);
    if (boardRequest.getIsSetNotice() == null)  board.setIsSetNotice(false);
    board.setCreateAccountId(account.getAccountId());

    if (filePart == null) {
      return boardDomainService.createBoard(board)
          .map(board1 -> BoardMapper.INSTANCE.toDto(board1, board1.getAccountDocs()
              == null || board1.getAccountDocs().size() < 1 ? new AdminAccount() : board1.getAccountDocs().get(0)))
          .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_CREATE_CONTENT)));
    } else {
      String fileKey = UUID.randomUUID().toString();
      board.setThumbnail(fileKey);

      return boardDomainService.createBoard(board)
          .zipWith(
              DataBufferUtils.join(filePart.content())
                  .flatMap(dataBuffer -> {
                    ByteBuffer buf = dataBuffer.asByteBuffer();
                    String fileName = filePart.filename();
                    Long fileSize = (long) buf.array().length;

                    return uploadFile(fileKey, fileName, fileSize, awsProperties.getBoardBucket(), buf)
                        .flatMap(res -> {
                          File info = File.builder()
                              .fileKey(fileKey)
                              .fileName(Normalizer.normalize(fileName, Normalizer.Form.NFC))
                              .createDate(LocalDateTime.now())
                              .delYn(false)
                              .build();
                          return fileDomainService.save(info);
                        });
                  })
          )
          .map(Tuple2::getT1)
          .map(board1 -> BoardMapper.INSTANCE.toDto(board1, board1.getAccountDocs()
              == null || board1.getAccountDocs().size() < 1 ? new AdminAccount() : board1.getAccountDocs().get(0)))
          .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_CREATE_CONTENT)));
    }
  }

  /**
   * ????????? ??????
   * @param filePart ?????????????????? ??????
   * @param boardRequest ?????????
   * @param account ??????
   * @return
   */
  public Mono<BoardResponse> updateBoard(FilePart filePart, BoardRequest boardRequest, Account account) {
    Long boardId = boardRequest.getId();
    if (boardRequest.getIsSetNotice() == null)  boardRequest.setIsSetNotice(false);

    if (filePart == null) {
      return boardDomainService.getBoardData(boardId)
          .flatMap(board -> {
            board.setCategory(boardRequest.getCategory());
            board.setTitle(boardRequest.getTitle());
            board.setContents(boardRequest.getContents());
            board.setDescription(boardRequest.getDescription());
            board.setIsSetNotice(boardRequest.getIsSetNotice());
            board.setTags(boardRequest.getTags());
            board.setThumbnail(boardRequest.getThumbnail());
            board.setContributor(boardRequest.getContributor());
            board.setUpdateAccountId(account.getAccountId());
            return boardDomainService.updateBoard(board);
          })
          .map(board1 -> BoardMapper.INSTANCE.toDto(board1, board1.getAccountDocs()
              == null || board1.getAccountDocs().size() < 1 ? new AdminAccount() : board1.getAccountDocs().get(0)))
          .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_UPDATE_CONTENT)));
    } else {
      String fileKey = UUID.randomUUID().toString();
      boardRequest.setThumbnail(fileKey);

      return boardDomainService.getBoardData(boardId)
          .flatMap(board -> {
            board.setCategory(boardRequest.getCategory());
            board.setTitle(boardRequest.getTitle());
            board.setContents(boardRequest.getContents());
            board.setDescription(boardRequest.getDescription());
            board.setIsSetNotice(boardRequest.getIsSetNotice());
            board.setTags(boardRequest.getTags());
            board.setThumbnail(boardRequest.getThumbnail());
            board.setContributor(boardRequest.getContributor());
            board.setUpdateAccountId(account.getAccountId());
            return boardDomainService.updateBoard(board);
          })
          .zipWith(
              DataBufferUtils.join(filePart.content())
                  .flatMap(dataBuffer -> {
                    ByteBuffer buf = dataBuffer.asByteBuffer();
                    String fileName = filePart.filename();
                    Long fileSize = (long) buf.array().length;

                    return uploadFile(fileKey, fileName, fileSize, awsProperties.getBoardBucket(), buf)
                        .flatMap(res -> {
                          File info = File.builder()
                              .fileKey(fileKey)
                              .fileName(Normalizer.normalize(fileName, Normalizer.Form.NFC))
                              .createDate(LocalDateTime.now())
                              .delYn(false)
                              .build();
                          return fileDomainService.save(info);
                        });
                  })
          )
          .map(Tuple2::getT1)
          .map(board1 -> BoardMapper.INSTANCE.toDto(board1, board1.getAccountDocs()
              == null || board1.getAccountDocs().size() < 1 ? new AdminAccount() : board1.getAccountDocs().get(0)))
          .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_UPDATE_CONTENT)));
    }
  }

  /**
   * ????????? ??????
   * @param boardId ?????????
   * @param account ??????
   * @return
   */
  public Mono<BoardResponse> deleteBoard(Long boardId, Account account) {
    return boardDomainService.getBoardData(boardId)
        .flatMap(board -> {
          board.setUpdateAccountId(account.getAccountId());
          return boardDomainService.deleteBoard(board);
        })
        .map(board1 -> BoardMapper.INSTANCE.toDto(board1, board1.getAccountDocs()
            == null || board1.getAccountDocs().size() < 1 ? new AdminAccount() : board1.getAccountDocs().get(0)))
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_DELETE_CONTENT)));
  }

  /**
   * ????????? ?????? ??????
   * @param deleteIds ????????? ID
   * @param account ??????
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

  /**
   * ????????? ?????? ?????????
   * @param fileParts ?????? ??????
   * @return
   */
  public Mono<UploaderData> uploadImage(Flux<FilePart> fileParts) {
    return fileParts.flatMap(filePart ->
        DataBufferUtils.join(filePart.content())
          .flatMap(dataBuffer -> {
            ByteBuffer buf = dataBuffer.asByteBuffer();
            String fileName = filePart.filename();
            Long fileSize = (long) buf.array().length;
            Boolean isImage = FileUtil.isImage(dataBuffer.asInputStream());
            String extension = FileUtil.getExtension(fileName);
            String fileKey = UUID.randomUUID() + "." + extension;

            String[] ALLOW_FILE_EXT = {"PNG", "GIF", "JPG", "JPEG", "PDF"};
            ValidationUtils.assertAllowFileExt(fileName, ALLOW_FILE_EXT);

            Long maxFileSize = Long.valueOf(100 * 1024 * 1024); // 100MB
            ValidationUtils.assertAllowFileSize(fileSize, maxFileSize);

            return uploadFile("files/" + fileKey, fileName, fileSize, awsProperties.getBoardBucket(), buf)
                .flatMap(res -> {
                  File info = File.builder()
                      .fileKey(fileKey)
                      .fileName(Normalizer.normalize(fileName, Normalizer.Form.NFC))
                      .createDate(LocalDateTime.now())
                      .delYn(false)
                      .build();
                  return fileDomainService.save(info)
                      .map(file -> UploaderDataInfo.builder()
                          .file(file.getFileKey())
                          .isImage(isImage)
                          .build()
                      );
                });
          }))
        .log()
        .collectList()
        .map(list ->
          UploaderData.builder()
              .files(list.stream().map(UploaderDataInfo::getFile).collect(Collectors.toList()))
              .isImages(list.stream().map(UploaderDataInfo::getIsImage).collect(Collectors.toList()))
              .baseurl(boardBucketUrl + "files/")
              .build()
        )
        .switchIfEmpty(Mono.error(new BoardException(ErrorCode.FAIL_CREATE_CONTENT)));
  }

  /**
   * S3 File Upload
   *
   * @param fileKey
   * @param fileName
   * @param fileSize
   * @param bucketName
   * @param content
   * @return
   */
  public Mono<PutObjectResponse> uploadFile(String fileKey, String fileName, Long fileSize, String bucketName, ByteBuffer content) {
    // String fileName = part.filename();
    log.debug("save => fileKey : " + fileKey);
    Map<String, String> metadata = new HashMap<String, String>();
    try {
      metadata.put("filename", URLEncoder.encode(fileName, "UTF-8"));
      metadata.put("filesize", String.valueOf(fileSize));
    } catch (UnsupportedEncodingException e) {
      return Mono.error(new BoardException(ErrorCode.FAIL_SAVE_FILE));
    }

    PutObjectRequest objectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .contentType((MediaType.APPLICATION_OCTET_STREAM).toString())
        .contentLength(fileSize)
        .metadata(metadata)
        .key(fileKey)
        .build();

    return Mono.fromFuture(
        s3AsyncClient.putObject(
            objectRequest, AsyncRequestBody.fromByteBuffer(content)
        ).whenComplete((resp, err) -> {
          try {
            if (resp != null) {
              log.info("upload success. Details {}", resp);
            } else {
              log.error("whenComplete error : {}", err);
              err.printStackTrace();
            }
          }finally {
            //s3AsyncClient.close();
          }
        }).thenApply(res -> {
          log.debug("putObject => {}", res);
          return res;
        })
    );
  }
}
