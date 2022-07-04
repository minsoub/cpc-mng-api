package com.bithumbsystems.persistence.mongodb.common.model.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "cpc_files")
public class File {
  @Id private String fileKey;
  private String fileName;
  private Boolean delYn;
  @CreatedDate private LocalDateTime createDate;
  @CreatedBy private String createAccountId;
  @LastModifiedDate private LocalDateTime updateDate;
  @LastModifiedBy private String updateAccountId;
}
