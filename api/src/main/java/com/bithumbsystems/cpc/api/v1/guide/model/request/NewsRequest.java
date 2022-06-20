package com.bithumbsystems.cpc.api.v1.guide.model.request;

import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class NewsRequest {

  private Long id;
  private String newspaper;
  private String title;
  private String thumbnailUrl;
  private String linkUrl;
  private LocalDate postingDate;
  private Boolean isUse;
}
