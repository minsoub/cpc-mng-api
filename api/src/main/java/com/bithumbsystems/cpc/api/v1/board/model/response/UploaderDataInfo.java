package com.bithumbsystems.cpc.api.v1.board.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UploaderDataInfo {
  private String file;
  private Boolean isImage;
}
