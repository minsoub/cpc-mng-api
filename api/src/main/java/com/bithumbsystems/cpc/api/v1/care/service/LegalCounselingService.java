package com.bithumbsystems.cpc.api.v1.care.service;

import com.bithumbsystems.cpc.api.core.config.property.AwsProperties;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.core.util.PageSupport;
import com.bithumbsystems.cpc.api.v1.care.exception.LegalCounselingException;
import com.bithumbsystems.cpc.api.v1.care.mapper.LegalCounselingMapper;
import com.bithumbsystems.cpc.api.v1.care.model.enums.Status;
import com.bithumbsystems.cpc.api.v1.care.model.request.LegalCounselingRequest;
import com.bithumbsystems.cpc.api.v1.care.model.response.LegalCounselingResponse;
import com.bithumbsystems.persistence.mongodb.care.model.entity.LegalCounseling;
import com.bithumbsystems.persistence.mongodb.care.service.LegalCounselingDomainService;
import com.bithumbsystems.persistence.mongodb.common.model.entity.File;
import com.bithumbsystems.persistence.mongodb.common.service.FileDomainService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class LegalCounselingService {

  private final AwsProperties awsProperties;

  private final LegalCounselingDomainService legalCounselingDomainService;
  private final S3AsyncClient s3AsyncClient;
  private final FileDomainService fileDomainService;

  /**
   * 법률 상담 신청(파일 업로드 후 법률 상담 정보 저장)
   * @param filePart 업로드 파일
   * @param legalCounselingRequest 법률 상담
   * @return
   */
  @Transactional
  public Mono<LegalCounseling> saveAll(FilePart filePart, LegalCounselingRequest legalCounselingRequest) {
    String fileKey = UUID.randomUUID().toString();
    AtomicReference<String> fileName = new AtomicReference<>();
    AtomicReference<Long> fileSize = new AtomicReference<>();

    fileName.set(filePart.filename());
    log.debug("Here is ....");

    return DataBufferUtils.join(filePart.content())
        .flatMap(dataBuffer -> {
          log.debug("dataBuffer join...");
          ByteBuffer buf = dataBuffer.asByteBuffer();
          log.debug("byte size ===> " + buf.array().length);

          fileSize.set((long) buf.array().length);

          return uploadFile(fileKey, fileName.toString(), fileSize.get(), awsProperties.getBucket(), buf)
              .flatMap(res -> {
                File info = File.builder()
                    .fileKey(fileKey)
                    .fileName(fileName.toString())
                    .delYn(false)
                    .build();
                return fileDomainService.save(info);
              });
        }).publishOn(Schedulers.boundedElastic()).flatMap(file -> {
          legalCounselingRequest.setAttachFileId(file.getFileKey());
          return saveLegalCounseling(legalCounselingRequest);
        });
  }

  /**
   * 법률 상담 등록
   * @param legalCounselingRequest 법률 상담
   * @return
   */
  public Mono<LegalCounseling> saveLegalCounseling(LegalCounselingRequest legalCounselingRequest) {
    LegalCounseling legalCounseling = LegalCounselingMapper.INSTANCE.toEntity(legalCounselingRequest);
    legalCounseling.setStatus(legalCounseling.getAnswerToContacts()? Status.REQUEST.getCode() : Status.REGISTER.getCode()); // 연락처로 답변받기 체크 시 '답변요청' 아니면 '접수' 상태
    return legalCounselingDomainService.createLegalCounseling(legalCounseling);
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
  private Mono<PutObjectResponse> uploadFile(String fileKey, String fileName, Long fileSize, String bucketName, ByteBuffer content) {
    log.debug("save => fileKey : " + fileKey);
    Map<String, String> metadata = new HashMap<String, String>();

    try {
      metadata.put("filename", URLEncoder.encode(fileName, "UTF-8"));
      metadata.put("filesize", String.valueOf(fileSize));
    } catch (UnsupportedEncodingException e) {
      return Mono.error(new LegalCounselingException(ErrorCode.FAIL_SAVE_FILE));
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
          } finally {
            //s3AsyncClient.close();
          }
        }).thenApply(res -> {
          log.debug("putObject => {}", res);
          return res;
        })
    );
  }

  /**
   * 파일 정보 조회
   * @param fileKey
   * @return
   */
  public Mono<File> getFileInfo(String fileKey) {
    return fileDomainService.findById(fileKey);
  }

  /**
   * 파일 다운로드
   * @param fileKey
   * @return
   */
  public Mono<InputStream> downloadFile(String fileKey) {

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(awsProperties.getBucket())
        .key(fileKey)
        .build();

    return Mono.fromFuture(
        s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes())
            .thenApply(bytes -> {
              return bytes.asInputStream();  // .asByteArray(); // ResponseBytes::asByteArray
            })
            .whenComplete((res, error) -> {
                  try {
                    if (res != null) {
                      log.debug("whenComplete -> {}", res);
                    }else {
                      error.printStackTrace();
                    }
                  } finally {
                    //s3AsyncClient.close();
                  }
                }
            )
    );
  }

  /**
   * 법률 상담 신청 목록 조회
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param keyword 키워드
   * @param page
   * @return
   */
  public Mono<PageSupport<LegalCounseling>> getLegalCounselingList(LocalDate fromDate, LocalDate toDate, String status, String keyword, Pageable page) {
    return legalCounselingDomainService.getLegalCounselingList(fromDate, toDate, status, keyword)
        .collectList()
        .map(list -> new PageSupport<>(
            list
                .stream()
                .sorted(Comparator
                    .comparingLong(LegalCounseling::getId)
                    .reversed())
                .skip((page.getPageNumber() - 1) * page.getPageSize())
                .limit(page.getPageSize())
                .collect(Collectors.toList()),
            page.getPageNumber(), page.getPageSize(), list.size()));
  }

  /**
   * 법률 상담 신청 조회
   * @param id ID
   * @return
   */
  public Mono<LegalCounselingResponse> getLegalCounselingData(Long id) {
    return legalCounselingDomainService.getLegalCounselingData(id).map(LegalCounselingMapper.INSTANCE::toDto)
        .switchIfEmpty(Mono.error(new LegalCounselingException(ErrorCode.NOT_FOUND_CONTENT)));
  }

  /**
   * 법률 상담 신청 수정
   * @param legalCounselingRequest 법률 상담 신청
   * @return
   */
  public Mono<LegalCounselingResponse> updateLegalCounseling(LegalCounselingRequest legalCounselingRequest) {
    Long id = legalCounselingRequest.getId();
    return legalCounselingDomainService.getLegalCounselingData(id)
        .flatMap(legalCounseling -> {
          legalCounseling.setAnswer(legalCounselingRequest.getAnswer());
          legalCounseling.setStatus(Status.COMPLETE.getCode()); // 답변완료 상태
          return legalCounselingDomainService.updateLegalCounseling(legalCounseling)
              .map(LegalCounselingMapper.INSTANCE::toDto);
        })
        .switchIfEmpty(Mono.error(new LegalCounselingException(ErrorCode.FAIL_UPDATE_CONTENT)))
        .doOnSuccess(legalCounselingResponse -> {
          if (legalCounselingResponse.getAnswerToContacts()) {
            // TODO: 메일 발송
            log.debug("TODO: 메일 발송");
          }
        });
  }

  /**
   * 법률 상담 신청 목록 엑셀 다운로드
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param keyword 키워드
   * @return
   */
  public Mono<ByteArrayInputStream> downloadExcel(LocalDate fromDate, LocalDate toDate, String status, String keyword) {
    return legalCounselingDomainService.getLegalCounselingList(fromDate, toDate, status, keyword)
        .switchIfEmpty(Mono.error(new LegalCounselingException(ErrorCode.NOT_FOUND_CONTENT)))
        .collectList()
        .flatMap(list -> this.createExcelFile(list));
  }

  /**
   * 엑셀 파일 생성
   * @param legalCounselingList 엑셀 데이터
   */
  public Mono<ByteArrayInputStream> createExcelFile(List<LegalCounseling> legalCounselingList) {
    return Mono.fromCallable(() -> {
          log.debug("엑셀 파일 생성 시작");

          SXSSFWorkbook workbook = new SXSSFWorkbook(SXSSFWorkbook.DEFAULT_WINDOW_SIZE);  // keep 100 rows in memory, exceeding rows will be flushed to disk
          ByteArrayOutputStream out = new ByteArrayOutputStream();

          CreationHelper creationHelper = workbook.getCreationHelper();

          Sheet sheet = workbook.createSheet("법률상담");

          Font headerFont = workbook.createFont();
          headerFont.setFontName("맑은 고딕");
          headerFont.setFontHeight((short) (10 * 20));
          headerFont.setBold(true);
          headerFont.setColor(IndexedColors.BLACK.index);

          Font bodyFont = workbook.createFont();
          bodyFont.setFontName("맑은 고딕");
          bodyFont.setFontHeight((short) (10 * 20));

          // Cell 스타일 생성
          CellStyle headerStyle = workbook.createCellStyle();
          headerStyle.setAlignment(HorizontalAlignment.CENTER);
          headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
          headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
          headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
          headerStyle.setFont(headerFont);

          // Row for Header
          Row headerRow = sheet.createRow(0);

          // Header
          String[] fields = {"번호", "상태", "이름", "이메일주소", "전화번호", "답변 내용"};
          for (int col = 0; col < fields.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(fields[col]);
            cell.setCellStyle(headerStyle);
          }

          // Body
          int rowIdx = 1;
          for (LegalCounseling legalCounseling : legalCounselingList) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(legalCounseling.getId());  // 번호
            row.createCell(1).setCellValue(Status.getTitle(legalCounseling.getStatus())); // 상태
            row.createCell(2).setCellValue(legalCounseling.getName());  // 이름
            row.createCell(3).setCellValue(legalCounseling.getEmail());  // 이메일주소
            row.createCell(4).setCellValue(legalCounseling.getCellPhone());  // 전화번호
            row.createCell(5).setCellValue(legalCounseling.getAnswer());  // 답변 내용
          }
          workbook.write(out);

          log.debug("엑셀 파일 생성 종료");
          return new ByteArrayInputStream(out.toByteArray());
        })
        .log();
  }
}
