package com.bithumbsystems.cpc.api.v1.main.model.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MainContentsRequest {
  private List<Long> virtualAssetTrends;
  private List<Long> blockchainNews;
  private String investmentGuide1Id;
  private List<Long> investmentGuide1;
  private String investmentGuide2Id;
  private List<Long> investmentGuide2;
  private String investmentGuide3Id;
  private List<Long> investmentGuide3;
}
