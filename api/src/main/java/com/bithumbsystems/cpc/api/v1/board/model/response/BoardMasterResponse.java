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
public class BoardMasterResponse {
  private String id;
  private String type;
  private Boolean isAllowComment;
  private Boolean isAllowReply;
  private Boolean isAllowAttachFile;
  private Boolean isUseCategory;
  private List<Category> categories;
  private String paginationType;
  private Integer countPerPage;
  private Boolean isUseTag;
  private List<String> tags;
  private Sns snsShare;
  private Auth auth;
  private LocalDateTime createDate;
}
