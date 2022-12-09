package com.bithumbsystems.cpc.api.v1.board.model.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BoardListResponse {
  private Long id;
  private List<String> category;
  private String title;
  private List<String> tags;
  private String thumbnail;
  private String description;
  private LocalDateTime createDate;
  private String createAccountId;
  private String createAccountName;
}
