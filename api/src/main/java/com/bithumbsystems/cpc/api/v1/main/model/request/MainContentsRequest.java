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
  private List<Long> digitalAssetBasic;
  private List<Long> insightColumn;
  private List<Long> digitalAssetTrends;
  private List<Long> blockchainNews;
}
