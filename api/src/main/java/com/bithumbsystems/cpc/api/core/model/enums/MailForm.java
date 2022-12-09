package com.bithumbsystems.cpc.api.core.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MailForm {
  DEFAULT("subject", "mail/default.html"),
  FRAUD_REPORT("[BITHUMB 고객지원센터] 신고하신 내용에 대해 답변 드립니다.", "templates/fraud-report.html"),
  LEGAL_COUNSELING("[BITHUMB 고객지원센터] 신청하신 내용에 대해 답변 드립니다.", "templates/legal-counseling.html"),
  EDUCATION_FORM("[BITHUMB 고객지원센터] 답변이 도착하였습니다.", "templates/education-mail.html");

  private final String subject;
  private final String path;
}
