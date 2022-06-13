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
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "cpc_main_contents")
public class MainContents {

  @Id private String id;
  private List<Long> virtualAssetTrends;
  private List<Long> blockchainNews;
  private String investmentGuide1Id;
  private List<Long> investmentGuide1;
  private String investmentGuide2Id;
  private List<Long> investmentGuide2;
  private String investmentGuide3Id;
  private List<Long> investmentGuide3;
  @CreatedDate private LocalDateTime createDate;
  @CreatedBy private String createAccountId;
  @LastModifiedDate private LocalDateTime updateDate;
  @LastModifiedBy private String updateAccountId;
  @Version private Integer version;
}
