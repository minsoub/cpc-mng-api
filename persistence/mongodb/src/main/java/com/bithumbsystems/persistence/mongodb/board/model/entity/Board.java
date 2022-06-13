package com.bithumbsystems.persistence.mongodb.board.model.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "boards")
public class Board {

  @Transient
  public static final String SEQUENCE_NAME = "boards_sequence";

  @Id private Long id;
  @Indexed private String boardMasterId;
  private String title;
  private String contents;
  private Boolean isReply;
  private String parentId;
  private Boolean isUse;
  private Integer readCount;
  private Boolean isSetNotice;
  private Boolean isSecret;
  private String password;
  private String attachFileId;
  private List<String> tags;
  private String thumbnail;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
}
