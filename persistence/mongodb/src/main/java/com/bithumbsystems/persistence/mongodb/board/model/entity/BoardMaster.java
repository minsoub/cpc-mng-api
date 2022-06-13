package com.bithumbsystems.persistence.mongodb.board.model.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "board_master")
public class BoardMaster {

  @Id private String id;
  private String siteId;
  private String name;
  private Boolean isUse;
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

  @Data
  public static class Category {
    private String categoryId;
    private String categoryName;
  }

  @Data
  public static class Sns {
    private Boolean kakaotalk;
    private Boolean facebook;
    private Boolean twitter;
    private Boolean url;
  }

  @Data
  public static class Auth {
    private String list;
    private String read;
    private String write;
    private String comment;
  }

  private LocalDateTime createDate;
  private LocalDateTime updateDate;
}
