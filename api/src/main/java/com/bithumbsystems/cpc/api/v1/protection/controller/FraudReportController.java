package com.bithumbsystems.cpc.api.v1.protection.controller;

import static com.bithumbsystems.cpc.api.core.util.PageSupport.DEFAULT_PAGE_SIZE;
import static com.bithumbsystems.cpc.api.core.util.PageSupport.FIRST_PAGE_NUM;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.bithumbsystems.cpc.api.core.model.response.SingleResponse;
import com.bithumbsystems.cpc.api.v1.protection.model.request.FraudReportRequest;
import com.bithumbsystems.cpc.api.v1.protection.service.FraudReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
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
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RestController
@RequestMapping("/fraud-report")
@RequiredArgsConstructor
@Tag(name = "Fraud Report APIs", description = "사기 신고 API")
public class FraudReportController {
  private final FraudReportService fraudReportService;

  /**
   * 사기 신고 등록
   * @param fraudReportRequest 사기 신고 정보
   * @param filePart 첨부 파일
   * @return
   */
  @PostMapping(value = "/", consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
  @Operation(description = "사기 신고 등록")
  public ResponseEntity<Mono<?>> createFraudReport(@RequestPart(value = "fraudReportRequest") FraudReportRequest fraudReportRequest,
      @RequestPart(value = "file", required = false) FilePart filePart) {

    return ResponseEntity.ok().body(fraudReportService.saveAll(filePart, fraudReportRequest).map(c -> new SingleResponse(c)));
  }

  /**
   * 사기 신고 목록 조회
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param status 상태
   * @param query 검색어
   * @param pageNo 페이지 번호
   * @param pageSize 페이지 당 표시 건수
   * @return
   */
  @GetMapping
  @Operation(description = "사기 신고 목록 조회")
  public ResponseEntity<Mono<?>> getFraudReportList(
      @RequestParam(name = "fromDate") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime fromDate,
      @RequestParam(name = "toDate") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime toDate,
      @RequestParam(name = "status") String status,
      @RequestParam(name = "query", required = false, defaultValue = "") String query,
      @RequestParam(name = "pageNo", defaultValue = FIRST_PAGE_NUM) int pageNo,
      @RequestParam(name = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize)
      throws UnsupportedEncodingException {
    String keyword = URLDecoder.decode(query, "UTF-8");
    log.info("keyword: {}", keyword);
    return ResponseEntity.ok().body(fraudReportService.getFraudReportList(fromDate, toDate, status, keyword, PageRequest.of(pageNo, pageSize))
        .map(response -> new SingleResponse(response)));
  }

  /**
   * 사기 신고 정보 조회
   * @param id ID
   * @return
   */
  @GetMapping(value = "/{id}")
  @Operation(description = "사기 신고 정보 조회")
  public ResponseEntity<Mono<?>> getFraudReportData(@PathVariable Long id) {
    return ResponseEntity.ok().body(fraudReportService.getFraudReportData(id)
        .map(response -> new SingleResponse(response)));
  }

  /**
   * 사기 신고 답변
   * @param fraudReportRequest 사기 신고 정보
   * @return
   */
  @PutMapping(value = "/{id}")
  @Operation(description = "사기 신고 답변")
  public ResponseEntity<Mono<?>> updateFraudReport(@RequestBody FraudReportRequest fraudReportRequest) {
    return ResponseEntity.ok().body(fraudReportService.updateFraudReport(fraudReportRequest)
        .map(response -> new SingleResponse(response)));
  }

  /**
   * 첨부 파일 다운로드
   * @param fileKey
   * @return
   */
  @GetMapping(value = "/download/{fileKey}", produces = APPLICATION_OCTET_STREAM_VALUE)
  public Mono<ResponseEntity<?>> downloadAttachedFile(@PathVariable String fileKey) {
    AtomicReference<String> fileName = new AtomicReference<>();

    return fraudReportService.getFileInfo(fileKey)
        .flatMap(res -> {
          log.debug("find file => {}", res);
          fileName.set(res.getFileName());
          // s3에서 파일을 다운로드 받는다.
          return fraudReportService.downloadFile(fileKey);
        })
        .log()
        .map(inputStream -> {
          log.debug("finaly result...here");
          HttpHeaders headers = new HttpHeaders();
          headers.setContentDispositionFormData(fileName.toString(), fileName.toString());
          ResponseEntity<?> entity = ResponseEntity.ok().cacheControl(CacheControl.noCache())
              .headers(headers)
              .body(new InputStreamResource(inputStream));
          return entity;
        });
  }

  /**
   * 엑셀 다운로드
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param status 상태
   * @param query 검색어
   * @return
   */
  @GetMapping(value = "/excel-download", produces = APPLICATION_OCTET_STREAM_VALUE)
  public Mono<ResponseEntity<?>> downloadExcel(
      @RequestParam(name = "fromDate") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime fromDate,
      @RequestParam(name = "toDate") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime toDate,
      @RequestParam(name = "status") String status,
      @RequestParam(name = "query", required = false, defaultValue = "") String query)
      throws UnsupportedEncodingException {

    String fileName = URLEncoder.encode("사기신고_다운로드.xlsx", "UTF-8");

    return fraudReportService.downloadExcel(fromDate, toDate, status, query)
        .log()
        .flatMap(inputStream -> {
          HttpHeaders headers = new HttpHeaders();
          headers.setContentDispositionFormData(fileName, fileName);
          return Mono.just(ResponseEntity.ok().cacheControl(CacheControl.noCache())
              .headers(headers)
              .body(new InputStreamResource(inputStream)));
        });
  }
}
