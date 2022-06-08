package com.bithumbsystems.cpc.api.v1.care.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.bithumbsystems.cpc.api.core.model.response.SingleResponse;
import com.bithumbsystems.cpc.api.v1.care.model.request.LegalCounselingRequest;
import com.bithumbsystems.cpc.api.v1.care.service.LegalCounselingService;
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
@RequestMapping("/legal-counseling")
@RequiredArgsConstructor
@Tag(name = "Legal Counseling APIs", description = "법률 상담 API")
public class LegalCounselingController {
  private final LegalCounselingService legalCounselingService;

  /**
   * 법률 상담 등록
   * @param legalCounselingRequest 법률 상담 신청 정보
   * @param filePart 첨부 파일
   * @return
   */
  @PostMapping(value = "/", consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
  @Operation(description = "법률 상담 등록")
  public ResponseEntity<Mono<?>> applyLegalCounseling(@RequestPart(value = "legalCounselingRequest") LegalCounselingRequest legalCounselingRequest,
      @RequestPart(value = "file", required = false) FilePart filePart) {

    return ResponseEntity.ok().body(legalCounselingService.saveAll(filePart, legalCounselingRequest).map(c -> new SingleResponse(c)));
  }
}
