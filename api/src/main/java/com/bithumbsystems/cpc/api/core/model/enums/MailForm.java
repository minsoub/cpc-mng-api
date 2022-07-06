package com.bithumbsystems.cpc.api.core.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MailForm {
  DEFAULT("subject", "mail/default.html"),
  FRAUD_REPORT("[빗썸 고객보호센터] 신고하신 내용에 대해 답변 드립니다.", "templates/fraud-report.html"),
  LEGAL_COUNSELING("[빗썸 고객보호센터] 신청하신 내용에 대해 답변 드립니다.", "templates/legal-counseling.html");

  private final String subject;
  private final String path;
}
