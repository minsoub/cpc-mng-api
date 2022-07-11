package com.bithumbsystems.cpc.api.v1.main.model.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SelectedBoardResponse {
  private String boardMasterId;
  private Long id;
  private String title;
  private LocalDateTime createDate;
}
