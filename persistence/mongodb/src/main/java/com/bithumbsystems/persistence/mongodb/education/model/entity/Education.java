package com.bithumbsystems.persistence.mongodb.education.model.entity;

import com.bithumbsystems.persistence.mongodb.accesslog.model.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document(collection = "cpc_education")
@AllArgsConstructor
@Data
@Builder
public class Education {
    @MongoId
    private String id;
    private String name;
    private String email;
    private String cellPhone;
    private String content;
    private LocalDateTime desireDate;
    private Boolean isAnswerComplete;
    //private Boolean isConsignmentAgreement;
    private Boolean isUseAgreement;
    private Boolean isEmail;
    private String answer;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String updateAccountId;
}
