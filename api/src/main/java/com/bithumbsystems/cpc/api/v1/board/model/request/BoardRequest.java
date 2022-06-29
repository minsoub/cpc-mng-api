package com.bithumbsystems.cpc.api.v1.board.model.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BoardRequest {
  private Long id;
  private String boardMasterId;
  private String title;
  private String contents;
  private Boolean isUse;
  private Boolean isSetNotice;
  private List<String> tags;
  private String thumbnail;
  private String description;
  private String category;
  private String contributor;
}
