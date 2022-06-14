package com.bithumbsystems.persistence.mongodb.protection.model.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "cpc_fraud_report")
public class FraudReport {

  @Transient
  public static final String SEQUENCE_NAME = "fraud_sequence";

  @Id private Long id;
  private String status;
  private String email;
  private String title;
  private String contents;
  private String attachFileId;
  private Boolean termsPrivacy;
  private Boolean answerToContacts;
  private String answer;
  @CreatedDate private LocalDateTime createDate;
  @CreatedBy private String createAccountId;
  @LastModifiedDate private LocalDateTime updateDate;
  @LastModifiedBy private String updateAccountId;
}
