package com.bithumbsystems.persistence.mongodb.protection.model.entity;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
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
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
}
