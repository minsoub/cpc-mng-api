package com.bithumbsystems.persistence.mongodb.board.model.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "cpc_boards")
public class Board {

  @Transient
  public static final String SEQUENCE_NAME = "cpc_boards_sequence";

  @Id private Long id;
  @Indexed private String boardMasterId;
  private String title;
  private String contents;
  private Boolean isUse;
  private Integer readCount;
  private Boolean isSetNotice;
  private List<String> tags;
  private String thumbnail;
  private String description;
  private String category;
  @CreatedDate private LocalDateTime createDate;
  @CreatedBy private String createAccountId;
  @LastModifiedDate private LocalDateTime updateDate;
  @LastModifiedBy private String updateAccountId;
}
