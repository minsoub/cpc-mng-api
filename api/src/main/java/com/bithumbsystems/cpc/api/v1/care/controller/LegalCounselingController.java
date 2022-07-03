package com.bithumbsystems.cpc.api.v1.care.controller;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.config.resolver.CurrentUser;
import com.bithumbsystems.cpc.api.core.model.response.MultiResponse;
import com.bithumbsystems.cpc.api.core.model.response.SingleResponse;
import com.bithumbsystems.cpc.api.v1.care.model.request.LegalCounselingRequest;
import com.bithumbsystems.cpc.api.v1.care.service.LegalCounselingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/legal-counseling")
@RequiredArgsConstructor
@Tag(name = "Legal Counseling APIs", description = "법률 상담 API")
public class LegalCounselingController {
  private final LegalCounselingService legalCounselingService;

  /**
   * 법률 상담 신청 목록 조회
   * @param fromDate 검색 시작일자
   * @param toDate 검색 종료일자
   * @param status 상태
   * @param query 검색어
   * @return
   */
  @GetMapping
  @Operation(summary = "법률 상담 신청 목록 조회", description = "법률 상담 관리: 법률 상담 신청 목록 조회", tags = "법률 상담 관리")
  public ResponseEntity<Mono<?>> getLegalCounselingList(
      @RequestParam(name = "from_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE) LocalDate fromDate,
      @RequestParam(name = "to_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE) LocalDate toDate,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "query", required = false, defaultValue = "") String query)
      throws UnsupportedEncodingException {
    String keyword = URLDecoder.decode(query, "UTF-8");
    log.info("keyword: {}", keyword);
    return ResponseEntity.ok().body(legalCounselingService.getLegalCounselingList(fromDate, toDate.plusDays(1), status, keyword)
        .collectList()
        .map(MultiResponse::new));
  }

  /**
   * 법률 상담 신청 정보 조회
   * @param id ID
   * @return
   */
  @GetMapping(value = "/{id}")
  @Operation(summary = "법률 상담 신청 정보 조회", description = "법률 상담 관리: 법률 상담 신청 정보 조회", tags = "법률 상담 관리")
  public ResponseEntity<Mono<?>> getLegalCounselingData(@PathVariable Long id) {
    return ResponseEntity.ok().body(legalCounselingService.getLegalCounselingData(id)
        .map(SingleResponse::new));
  }

  /**
   * 법률 상담 신청 답변
   * @param fraudReportRequest 법률 상담 신청 정보
   * @param account 계정
   * @return
   */
  @PutMapping(value = "/{id}")
  @Operation(summary = "법률 상담 신청 답변", description = "법률 상담 관리: 법률 상담 신청 답변 등록", tags = "법률 상담 관리")
  public ResponseEntity<Mono<?>> updateLegalCounseling(@RequestBody LegalCounselingRequest fraudReportRequest,
      @Parameter(hidden = true) @CurrentUser Account account) {
    return ResponseEntity.ok().body(legalCounselingService.updateLegalCounseling(fraudReportRequest, account)
        .map(SingleResponse::new));
  }

  /**
   * 첨부 파일 다운로드
   * @param fileKey
   * @return
   */
  @GetMapping(value = "/download/{fileKey}", produces = APPLICATION_OCTET_STREAM_VALUE)
  @Operation(summary = "첨부 파일 다운로드", description = "법률 상담 관리: 첨부 파일 다운로드", tags = "법률 상담 관리")
  public Mono<ResponseEntity<?>> downloadAttachedFile(@PathVariable String fileKey) {
    AtomicReference<String> fileName = new AtomicReference<>();

    return legalCounselingService.getFileInfo(fileKey)
        .flatMap(res -> {
          log.debug("find file => {}", res);
          fileName.set(res.getFileName());
          // s3에서 파일을 다운로드 받는다.
          return legalCounselingService.downloadFile(fileKey);
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
  @Operation(summary = "엑셀 다운로드", description = "법률 상담 관리: 엑셀 다운로드", tags = "법률 상담 관리")
  public Mono<ResponseEntity<?>> downloadExcel(
      @RequestParam(name = "from_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE) LocalDate fromDate,
      @RequestParam(name = "to_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = ISO.DATE) LocalDate toDate,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "query", required = false, defaultValue = "") String query)
      throws UnsupportedEncodingException {

    String fileName = URLEncoder.encode("법률상담신청_다운로드.xlsx", "UTF-8");

    return legalCounselingService.downloadExcel(fromDate, toDate.plusDays(1), status, query)
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
