package com.bithumbsystems.cpc.api.v1.education.controller;

import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.config.resolver.CurrentUser;
import com.bithumbsystems.cpc.api.core.exception.InvalidParameterException;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.core.model.response.MultiResponse;
import com.bithumbsystems.cpc.api.core.model.response.SingleResponse;
import com.bithumbsystems.cpc.api.v1.education.model.request.EducationRequest;
import com.bithumbsystems.cpc.api.v1.education.service.EducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    /**
     * 신청자 관리 목록 조회 - 마스킹
     *
     * @param startDate
     * @param endDate
     * @param isAnswerComplete
     * @param keyword
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/masking")
    @Operation(summary = "신청자 관리 목록 조회-마스킹", description = "찾아가는 교육 관리 > 신청자 관리 목록 조회", tags = "찾아가는 교육 관리 > 신청자 관리 목록 조회")
    public ResponseEntity<Mono<?>> getEducationMaskingList(
            @RequestParam(name = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "is_answer_complete", required = false) String isAnswerComplete,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword)
            throws UnsupportedEncodingException {
        String word = URLDecoder.decode(keyword, "UTF-8");
        Boolean answerComplete = null;
        if (StringUtils.hasLength(isAnswerComplete)) {
            if (isAnswerComplete.equals("true")) answerComplete = Boolean.TRUE;
            else if (isAnswerComplete.equals("false")) answerComplete = Boolean.FALSE;
        }
        log.info("keyword: {}", keyword.replaceAll("[\r\n]",""));
        return ResponseEntity.ok().body(educationService.searchList(startDate, endDate.plusDays(1), answerComplete, word)
                //.collectList()
                .map(MultiResponse::new));
    }

    /**
     * 신청자 관리 목록 조회 - 마스킹 해제
     *
     * @param startDate
     * @param endDate
     * @param isAnswerComplete
     * @param keyword
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/unmasking")
    @Operation(summary = "신청자 관리 목록 조회-마스킹 해제", description = "찾아가는 교육 관리 > 신청자 관리 목록 조회", tags = "찾아가는 교육 관리 > 신청자 관리 목록 조회")
    public ResponseEntity<Mono<?>> getEducationUnMaskingList(
            @RequestParam(name = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "is_answer_complete", required = false) String isAnswerComplete,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "reason", required = true, defaultValue = "") String reason,
            @Parameter(hidden = true) @CurrentUser Account account
    )
            throws UnsupportedEncodingException {
        String word = URLDecoder.decode(keyword, "UTF-8");
        String reasonContent = URLDecoder.decode(reason, "UTF-8");
        Boolean answerComplete = null;
        if (StringUtils.hasLength(isAnswerComplete)) {
            if (isAnswerComplete.equals("true")) answerComplete = Boolean.TRUE;
            else if (isAnswerComplete.equals("false")) answerComplete = Boolean.FALSE;
        }
        log.info("keyword: {}", keyword.replaceAll("[\r\n]",""));
        return ResponseEntity.ok().body(educationService.searchListUnmasking(startDate, endDate.plusDays(1), answerComplete, word, reasonContent, account)
                //.collectList()
                .map(MultiResponse::new));
    }
    /**
     * 신청자 관리 상세 조회 - 마스킹 상태
     *
     * @param id
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/{id}/masking")
    @Operation(summary = "신청자 관리 상세 조회-마스킹", description = "찾아가는 교육 관리 > 신청자 관리 상세 조회", tags = "찾아가는 교육 관리 > 신청자 관리 상세 조회")
    public ResponseEntity<Mono<?>> getEducationMasking(@Parameter(name = "id", description = "신청자관리 id", in = ParameterIn.PATH)
                                                           @PathVariable("id") String id)
            throws UnsupportedEncodingException {
        return ResponseEntity.ok().body(educationService.findById(id)
                .map(SingleResponse::new));
    }

    /**
     * 신청자 관리 상세 조회 - 마스킹 해제
     *
     * @param id
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/{id}/unmasking")
    @Operation(summary = "신청자 관리 상세 조회-마스킹 해제", description = "찾아가는 교육 관리 > 신청자 관리 상세 조회", tags = "찾아가는 교육 관리 > 신청자 관리 상세 조회")
    public ResponseEntity<Mono<?>> getEducationUnMasking(@Parameter(name = "id", description = "신청자관리 id", in = ParameterIn.PATH)
                                                             @PathVariable("id") String id,
                                                         @RequestParam(name = "reason", required = true, defaultValue = "") String reason,
                                                         @Parameter(hidden = true) @CurrentUser Account account)
            throws UnsupportedEncodingException {
        String word = URLDecoder.decode(reason, "UTF-8");
        return ResponseEntity.ok().body(educationService.findByIdUnmasking(id, word, account)
                .map(SingleResponse::new));
    }


    /**
     * 신청자 관리 답변 저장
     *
     * @param educationRequest
     * @return
     */
    @PutMapping("/{id}")
    @Operation(summary = "신청자 관리 답변 저장", description = "찾아가는 교육 관리 > 신청자 관리 답변 저장", tags = "찾아가는 교육 관리 > 신청자 관리 답변 저장")
    public ResponseEntity<Mono<?>> saveEducation(@Parameter(name = "project Object", description = "프로젝트 의 모든 정보", in = ParameterIn.PATH)
                                                     @RequestBody EducationRequest educationRequest,
                                                 @Parameter(hidden = true) @CurrentUser Account account) {
        // Validation check
        if (educationRequest.getIsMasking() == null) {
            throw new InvalidParameterException(ErrorCode.INVALID_PARAMETER_FORMAT);
        }
        if (!StringUtils.hasLength(educationRequest.getAnswer())) {
            throw new InvalidParameterException(ErrorCode.NOT_FOUND_CONTENT);
        }
        if (!StringUtils.hasLength(educationRequest.getAnswer())) {
            throw new InvalidParameterException(ErrorCode.NOT_FOUND_CONTENT);
        }
        return ResponseEntity.ok().body(educationService.save(educationRequest, account)
                .map(c -> new SingleResponse(c))
        );
    }

}
