package com.bithumbsystems.persistence.mongodb.protection.model.entity;

import java.time.LocalDateTime;
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

  @Id
  private Long id;

  private String status;
  private String title;
  private String contents;
  private String attachFileId;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
}
