package com.bithumbsystems.cpc.api.v1.protection.service;

import com.bithumbsystems.cpc.api.core.config.property.AwsProperties;
import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.exception.MailException;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.core.model.enums.MailForm;
import com.bithumbsystems.cpc.api.core.util.AES256Util;
import com.bithumbsystems.cpc.api.core.util.DateUtil;
import com.bithumbsystems.cpc.api.core.util.message.MailSenderInfo;
import com.bithumbsystems.cpc.api.core.util.message.MessageService;
import com.bithumbsystems.cpc.api.v1.accesslog.request.AccessLogRequest;
import com.bithumbsystems.cpc.api.v1.protection.exception.FraudReportException;
import com.bithumbsystems.cpc.api.v1.protection.mapper.FraudReportMapper;
import com.bithumbsystems.cpc.api.v1.protection.model.enums.Status;
import com.bithumbsystems.cpc.api.v1.protection.model.request.FraudReportRequest;
import com.bithumbsystems.cpc.api.v1.protection.model.response.FraudReportResponse;
import com.bithumbsystems.persistence.mongodb.accesslog.model.enums.ActionType;
import com.bithumbsystems.persistence.mongodb.common.model.entity.File;
import com.bithumbsystems.persistence.mongodb.common.service.FileDomainService;
import com.bithumbsystems.persistence.mongodb.protection.model.entity.FraudReport;
import com.bithumbsystems.persistence.mongodb.protection.service.FraudReportDomainService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudReportService {

  private final AwsProperties awsProperties;

  private final FraudReportDomainService fraudReportDomainService;
  private final S3AsyncClient s3AsyncClient;
  private final FileDomainService fileDomainService;

  private final MessageService messageService;
  private final SpringTemplateEngine templateEngine;

  @Value("${webserver.url}")
  String webRootUrl;

  private final ApplicationEventPublisher applicationEventPublisher;

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
   * 사기 신고 목록 조회
   * @param startDate 검색 시작일자
   * @param endDate 검색 종료일자
   * @param keyword 키워드
   * @param account 계정
   * @return
   */
  public Flux<FraudReportResponse> getFraudReportList(LocalDate startDate, LocalDate endDate, String status, String keyword, Account account) {
    return fraudReportDomainService.findBySearchText(startDate, endDate, status, keyword)
        .map(fraudReport -> {
          fraudReport.setEmail(AES256Util.decryptAES(awsProperties.getKmsKey(), fraudReport.getEmail()));
          return FraudReportMapper.INSTANCE.toDto(fraudReport, fraudReport.getFileDocs()
              == null || fraudReport.getFileDocs().size() < 1 ? new File() : fraudReport.getFileDocs().get(0));
        })
        .doOnComplete(() -> sendPrivacyAccessLog(ActionType.VIEW, null, account));
  }

  /**
   * 사기 신고 조회
   * @param id ID
   * @param account 계정
   * @return
   */
  public Mono<FraudReportResponse> getFraudReportData(Long id, Account account) {
    return fraudReportDomainService.getFraudReportData(id)
        .map(fraudReport -> {
          fraudReport.setEmail(AES256Util.decryptAES(awsProperties.getKmsKey(), fraudReport.getEmail()));
          return FraudReportMapper.INSTANCE.toDto(fraudReport, fraudReport.getFileDocs()
              == null || fraudReport.getFileDocs().size() < 1 ? new File() : fraudReport.getFileDocs().get(0));
        })
        .switchIfEmpty(Mono.error(new FraudReportException(ErrorCode.NOT_FOUND_CONTENT)))
        .doFinally(v -> sendPrivacyAccessLog(ActionType.VIEW, null, account));
  }

  /**
   * 사기 신고 수정
   * @param fraudReportRequest 사기 신고
   * @param account 계정
   * @return
   */
  public Mono<FraudReportResponse> updateFraudReport(FraudReportRequest fraudReportRequest, Account account) {
    Long id = fraudReportRequest.getId();
    return fraudReportDomainService.getFraudReportData(id)
        .log()
        .flatMap(fraudReport -> {
          fraudReport.setAnswer(fraudReportRequest.getAnswer());
          fraudReport.setSendToEmail(fraudReportRequest.getSendToEmail());
          fraudReport.setStatus(Status.COMPLETE.getCode()); // 답변완료 상태
          fraudReport.setUpdateAccountId(account.getAccountId());
          return fraudReportDomainService.updateFraudReport(fraudReport)
              .map(fraudReport1 -> {
                fraudReport1.setEmail(AES256Util.decryptAES(awsProperties.getKmsKey(), fraudReport1.getEmail()));
                return FraudReportMapper.INSTANCE.toDto(fraudReport1, fraudReport1.getFileDocs()
                    == null || fraudReport1.getFileDocs().size() < 1 ? new File() : fraudReport1.getFileDocs().get(0));
              });
        })
        .switchIfEmpty(Mono.error(new FraudReportException(ErrorCode.FAIL_UPDATE_CONTENT)))
        .doOnSuccess(fraudReportResponse -> {
          if (fraudReportResponse.getSendToEmail()) {
            sendMail(fraudReportResponse.getEmail(), fraudReportResponse.getAnswer());
          }
        });
  }

  /**
   * 사기 신고 목록 엑셀 다운로드
   * @param startDate 검색 시작일자
   * @param endDate 검색 종료일자
   * @param keyword 키워드
   * @param reason 다운로드 사유
   * @param account 계정
   * @return
   */
  public Mono<ByteArrayInputStream> downloadExcel(LocalDate startDate, LocalDate endDate, String status, String keyword, String reason, Account account) {
    return fraudReportDomainService.findBySearchText(startDate, endDate, status, keyword)
        .map(fraudReport -> {
          fraudReport.setEmail(AES256Util.decryptAES(awsProperties.getKmsKey(), fraudReport.getEmail()));
          return fraudReport;
        })
        .switchIfEmpty(Mono.error(new FraudReportException(ErrorCode.NOT_FOUND_CONTENT)))
        .collectList()
        .flatMap(list -> this.createExcelFile(list))
        .doFinally(v -> sendPrivacyAccessLog(ActionType.DOWNLOAD, reason, account));
  }

  /**
   * 엑셀 파일 생성
   * @param fraudReportList 엑셀 데이터
   */
  public Mono<ByteArrayInputStream> createExcelFile(List<FraudReport> fraudReportList) {
    return Mono.fromCallable(() -> {
      log.debug("엑셀 파일 생성 시작");

      SXSSFWorkbook workbook = new SXSSFWorkbook(SXSSFWorkbook.DEFAULT_WINDOW_SIZE);  // keep 100 rows in memory, exceeding rows will be flushed to disk
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      CreationHelper creationHelper = workbook.getCreationHelper();

      Sheet sheet = workbook.createSheet("사기신고");

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
      String[] fields = {"번호", "상태", "내용", "첨부파일", "등록일시", "제보자", "답변 내용"};
      for (int col = 0; col < fields.length; col++) {
        Cell cell = headerRow.createCell(col);
        cell.setCellValue(fields[col]);
        cell.setCellStyle(headerStyle);
      }

      // Body
      int rowIdx = 1;
      for (FraudReport fraudReport : fraudReportList) {
        Row row = sheet.createRow(rowIdx++);

        row.createCell(0).setCellValue(fraudReport.getId());  // 번호
        row.createCell(1).setCellValue(Status.getTitle(fraudReport.getStatus())); // 상태
        row.createCell(2).setCellValue(fraudReport.getContents());  // 내용
        row.createCell(3).setCellValue(
            StringUtils.isEmpty(fraudReport.getAttachFileId()) ? "N" : "Y");  // 첨부파일
        row.createCell(4).setCellValue(DateUtil.toString(fraudReport.getCreateDate()));  // 등록일시
        row.createCell(5).setCellValue(fraudReport.getEmail()); // 제보자
        row.createCell(6).setCellValue(fraudReport.getAnswer());  // 답변 내용
      }
      workbook.write(out);

      log.debug("엑셀 파일 생성 종료");
      return new ByteArrayInputStream(out.toByteArray());
    })
    .log();
  }

  /**
   * 메일 발송
   * @param email
   */
  private void sendMail(String email, String contents) {
    try {
      Context context = new Context();
      context.setVariable("email", email);
      context.setVariable("contents", contents);
      context.setVariable("imgHeaderUrl", webRootUrl + "img/email/header.png");
      context.setVariable("imgFooterUrl", webRootUrl + "img/email/footer.png");

      String html = templateEngine.process("fraud-report", context);
      log.info("send mail: " + html);

      messageService.send(
          MailSenderInfo.builder()
              .bodyHTML(html)
              .subject(MailForm.FRAUD_REPORT.getSubject())
              .emailAddress(email)
              .build()
      );
    } catch (MessagingException | IOException e) {
      throw new MailException(ErrorCode.FAIL_SEND_MAIL);
    }
  }

  /**
   * 개인정보 접근 로그 발송
   * @param account
   */
  private void sendPrivacyAccessLog(ActionType actionType, String reason, Account account) {
    applicationEventPublisher.publishEvent(
        AccessLogRequest.builder()
            .email(account.getEmail())
            .accountId(account.getAccountId())
            .ip(account.getUserIp())
            .actionType(actionType)
            .reason(reason)
            .description("고객보호센터 - 사기 신고")
            .siteId(account.getMySiteId())
            .build()
    );
  }
}
