package com.bithumbsystems.persistence.mongodb.guide.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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
@Document(collection = "cpc_news")
public class News {

  @Transient
  public static final String SEQUENCE_NAME = "news_sequence";

  @Id private Long id;
  private String newspaper;
  private String title;
  private String thumbnailUrl;
  private String linkUrl;
  private LocalDate postingDate;
  private Boolean isUse;
  @CreatedDate private LocalDateTime createDate;
  @CreatedBy private String createAccountId;
  @LastModifiedDate private LocalDateTime updateDate;
  @LastModifiedBy private String updateAccountId;
}
