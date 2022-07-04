package com.bithumbsystems.cpc.api.v1.main.model.response;

import com.bithumbsystems.cpc.api.v1.board.model.response.BoardResponse;
import com.bithumbsystems.cpc.api.v1.guide.model.response.NewsResponse;
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
  private List<BoardResponse> digitalAssetBasic;
  private List<BoardResponse> insightColumn;
  private List<BoardResponse> digitalAssetTrends;
  private List<NewsResponse> blockchainNews;
}
