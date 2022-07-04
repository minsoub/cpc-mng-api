package com.bithumbsystems.cpc.api.v1.care.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LegalCounselingRequest {
  private Long id;
  private String name;
  private String email;
  private String cellPhone;
  private String contents;
  private Boolean servicePrivacy;
  private Boolean termsPrivacy;
  private Boolean answerToContacts;
  private String answer;
  private Boolean sendToEmail;
  private String attachFileId;
}
