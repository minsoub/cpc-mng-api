package com.bithumbsystems.persistence.mongodb.care.model.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "cpc_legal_counseling")
public class LegalCounseling {

  @Transient
  public static final String SEQUENCE_NAME = "counseling_sequence";

  @Id private Long id;
  private String status;
  private String name;
  private String email;
  private String contents;
  private String cellPhone;
  private String attachFileId;
  private Boolean servicePrivacy;
  private Boolean termsPrivacy;
  private Boolean answerToContacts;
  private String answer;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
}
