package com.bithumbsystems.cpc.api.v1.protection.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FraudReportRequest {
  private String email;
  private String title;
  private String contents;
  private Boolean termsPrivacy;
  private Boolean answerToContacts;
  private String attachFileId;
}
