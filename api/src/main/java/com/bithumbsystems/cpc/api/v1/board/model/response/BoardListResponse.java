package com.bithumbsystems.cpc.api.v1.board.model.response;

import java.time.LocalDateTime;
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
  private String title;
  private LocalDateTime createDate;
}
