package com.bithumbsystems.persistence.mongodb.care.model.entity;

import com.bithumbsystems.persistence.mongodb.common.model.entity.File;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "cpc_legal_counseling")
public class LegalCounseling {

  @Transient
  public static final String SEQUENCE_NAME = "cpc_counseling_sequence";

  @Id private Long id;
  private String status;
  private String name;
  private String email;
  private String contents;
  private String cellPhone;
  private String attachFileId;
  private Boolean entrustPrivacy;
  private Boolean termsPrivacy;
  private Boolean answerToContacts;
  private String answer;
  private Boolean sendToEmail;
  @CreatedDate private LocalDateTime createDate;
  @CreatedBy private String createAccountId;
  @LastModifiedDate private LocalDateTime updateDate;
  @LastModifiedBy private String updateAccountId;
  @DBRef(db = "cpc_files") private List<File> fileDocs;
}
