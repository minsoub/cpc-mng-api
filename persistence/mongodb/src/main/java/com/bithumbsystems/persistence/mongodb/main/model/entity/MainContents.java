package com.bithumbsystems.persistence.mongodb.main.model.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "cpc_main_contents")
public class MainContents {

  @Id private String id;
  private List<Long> digitalAssetBasic; // 빗썸경제연구소
  private List<Long> insightColumn; // 오피니언컬럼
  private List<Long> digitalAssetTrends; // 이지코노미
  private List<Long> blockchainNews;
  @CreatedDate private LocalDateTime createDate;
  @CreatedBy private String createAccountId;
  @LastModifiedDate private LocalDateTime updateDate;
  @LastModifiedBy private String updateAccountId;
}
