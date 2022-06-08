package com.bithumbsystems.persistence.mongodb.common.model.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "files")
public class File {
  @Id
  private String fileKey;
  private String fileName;
  private Boolean delYn;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
}
