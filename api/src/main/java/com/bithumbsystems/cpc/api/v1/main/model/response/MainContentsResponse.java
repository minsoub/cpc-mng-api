package com.bithumbsystems.cpc.api.v1.main.model.response;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MainContentsResponse {

  private Long id;
  private ArrayList<Long> virtualAssetTrends;
  private ArrayList<Long> blockchainNews;
  private String investmentGuide1Id;
  private ArrayList<Long> investmentGuide1;
  private String investmentGuide2Id;
  private ArrayList<Long> investmentGuide2;
  private String investmentGuide3Id;
  private ArrayList<Long> investmentGuide3;
}
