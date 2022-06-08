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
public class NewsResponse {
  private Long id;
  private String title;
  private String newspaper;
  private String thumbnailUrl;
  private String linkUrl;
  private LocalDateTime createDate;
}
