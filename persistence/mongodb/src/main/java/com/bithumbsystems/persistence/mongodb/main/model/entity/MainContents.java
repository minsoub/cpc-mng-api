package com.bithumbsystems.persistence.mongodb.main.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "cpc_main_contents")
public class MainContents {

  @Id
  private String id;

  private ArrayList<Long> virtualAssetTrends;
  private ArrayList<Long> blockchainNews;
  private String investmentGuide1Id;
  private ArrayList<Long> investmentGuide1;
  private String investmentGuide2Id;
  private ArrayList<Long> investmentGuide2;
  private String investmentGuide3Id;
  private ArrayList<Long> investmentGuide3;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
}
