package com.bithumbsystems.cpc.api.v1.board.model.response;

import com.bithumbsystems.persistence.mongodb.board.model.entity.BoardMaster.Auth;
import com.bithumbsystems.persistence.mongodb.board.model.entity.BoardMaster.Category;
import com.bithumbsystems.persistence.mongodb.board.model.entity.BoardMaster.Sns;
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
public class BoardMasterListResponse {
  private String id;
  private String siteId;
  private String siteName;
  private String name;
  private String type;
  private String typeName;
  private Boolean isUseTag;
  private LocalDateTime createDate;
}
