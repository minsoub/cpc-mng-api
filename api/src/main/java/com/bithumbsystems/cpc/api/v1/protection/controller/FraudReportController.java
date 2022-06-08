package com.bithumbsystems.cpc.api.v1.protection.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.bithumbsystems.cpc.api.core.model.response.SingleResponse;
import com.bithumbsystems.cpc.api.v1.protection.model.request.FraudReportRequest;
import com.bithumbsystems.cpc.api.v1.protection.service.FraudReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
  public ResponseEntity<Mono<?>> createBoard(@RequestPart(value = "fraudReportRequest") FraudReportRequest fraudReportRequest,
      @RequestPart(value = "file", required = false) FilePart filePart) {

    return ResponseEntity.ok().body(fraudReportService.saveAll(filePart, fraudReportRequest).map(c -> new SingleResponse(c)));

  }
}
