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
  private List<Long> virtualAssetBasic;
  private List<Long> insightColumn;
  private List<Long> virtualAssetTrends;
  private List<Long> blockchainNews;
}
