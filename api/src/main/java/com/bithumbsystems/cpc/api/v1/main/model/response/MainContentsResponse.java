package com.bithumbsystems.cpc.api.v1.main.model.response;

import com.bithumbsystems.cpc.api.v1.board.model.response.BoardResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MainContentsResponse {

  private List<BoardResponse> virtualAssetTrends;
  private List<BoardResponse> blockchainNews;
  private String investmentGuide1Id;
  private List<BoardResponse> investmentGuide1;
  private String investmentGuide2Id;
  private List<BoardResponse> investmentGuide2;
  private String investmentGuide3Id;
  private List<BoardResponse> investmentGuide3;
}
