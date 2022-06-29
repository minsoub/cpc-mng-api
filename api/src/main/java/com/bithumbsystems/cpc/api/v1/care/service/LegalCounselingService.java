package com.bithumbsystems.cpc.api.v1.care.service;

import com.bithumbsystems.cpc.api.core.config.property.AwsProperties;
import com.bithumbsystems.cpc.api.core.config.resolver.Account;
import com.bithumbsystems.cpc.api.core.exception.MailException;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.core.model.enums.MailForm;
import com.bithumbsystems.cpc.api.core.util.FileUtil;
import com.bithumbsystems.cpc.api.core.util.message.MailSenderInfo;
import com.bithumbsystems.cpc.api.core.util.message.MessageService;
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
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import javax.mail.MessagingException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class LegalCounselingService {

  private final AwsProperties awsProperties;

  private final LegalCounselingDomainService legalCounselingDomainService;
  private final S3AsyncClient s3AsyncClient;
  private final FileDomainService fileDomainService;

  private final MessageService messageService;

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
   * @param pageRequest 페이지 정보
   * @return
   */
  public Mono<Page<LegalCounseling>> getLegalCounselingList(LocalDate fromDate, LocalDate toDate, String status, String keyword, PageRequest pageRequest) {
    return legalCounselingDomainService.findPageBySearchText(fromDate, toDate, status, keyword, pageRequest)
        .collectList()
        .zipWith(legalCounselingDomainService.countBySearchText(fromDate, toDate, status, keyword)
            .map(c -> c))
        .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()));
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
   * @param account 계정
   * @return
   */
  public Mono<LegalCounselingResponse> updateLegalCounseling(LegalCounselingRequest legalCounselingRequest, Account account) {
    Long id = legalCounselingRequest.getId();
    return legalCounselingDomainService.getLegalCounselingData(id)
        .flatMap(legalCounseling -> {
          legalCounseling.setAnswer(legalCounselingRequest.getAnswer());
          legalCounseling.setStatus(Status.COMPLETE.getCode()); // 답변완료 상태
          legalCounseling.setUpdateAccountId(account.getAccountId());
          return legalCounselingDomainService.updateLegalCounseling(legalCounseling)
              .map(LegalCounselingMapper.INSTANCE::toDto);
        })
        .switchIfEmpty(Mono.error(new LegalCounselingException(ErrorCode.FAIL_UPDATE_CONTENT)))
        .doOnSuccess(legalCounselingResponse -> {
          if (legalCounselingResponse.getAnswerToContacts()) {
            sendMail(legalCounselingResponse.getEmail(), legalCounselingResponse.getAnswer());
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
    return legalCounselingDomainService.findBySearchText(fromDate, toDate, status, keyword)
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



  /**
   * 메일 발송
   * @param email
   */
  private void sendMail(String email, String contents) {
    try {
      String html = FileUtil.readResourceFile(MailForm.LEGAL_COUNSELING.getPath())
          .replace("${{subject}}", MailForm.LEGAL_COUNSELING.getSubject())
          .replace("${{contents}}", contents);
      log.info("send mail: " + html);

      messageService.send(
          MailSenderInfo.builder()
              .bodyHTML(html)
              .subject(MailForm.LEGAL_COUNSELING.getSubject())
              .emailAddress(email)
              .build()
      );
    } catch (MessagingException | IOException e) {
      throw new MailException(ErrorCode.FAIL_SEND_MAIL);
    }
  }
}
