package com.bithumbsystems.cpc.api.v1.care.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LegalCounselingResponse {
  private Long id;
  private String status;
  private String name;
  private String email;
  private String cellPhone;
  private String contents;
  private String attachFileId;
  private Boolean servicePrivacy;
  private Boolean termsPrivacy;
  private Boolean answerToContacts;
  private String answer;
  private LocalDateTime createDate;
}
