package com.bithumbsystems.persistence.mongodb.guide.model.entity;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
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
  private Date postingDate;
  private Boolean isUse;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
}
