package com.bithumbsystems.cpc.api.v1.protection.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FraudReportResponse {
  private Long id;
  private String status;
  private String email;
  private String title;
  private String contents;
  private Boolean entrustPrivacy;
  private Boolean termsPrivacy;
  private Boolean answerToContacts;
  private String answer;
  private Boolean sendToEmail;
  private String attachFileId;
  private String attachFileName;
  private LocalDateTime createDate;
}
