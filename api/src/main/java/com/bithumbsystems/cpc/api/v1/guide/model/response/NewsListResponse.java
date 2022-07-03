package com.bithumbsystems.cpc.api.v1.guide.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class NewsListResponse {
  private Long id;
  private String newspaper;
  private String title;
  private String linkUrl;
  private String postingDate;
  private LocalDateTime createDate;
  private String createAccountId;
  private String createAccountName;
}
