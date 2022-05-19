package com.bithumbsystems.persistence.mongodb.faq.content.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class FaqContent {

    @Id
    private String id;
    private String userId;      //사용자 id
    private String order;       //노출순서
    private String category;    //카테고리
    private String title;       //제목
    private String content;     //내용
    private Boolean useYn;       //사용여부
    private String costumer;    //등록자
    private String language;    //언어


    @CreatedDate
    private LocalDateTime createDate; //생성날짜
    private String createAdminAccountId;  //생성자 id

    @LastModifiedDate
    private LocalDateTime updateDate; //수정날짜
    @CreatedBy
    private LocalDateTime createAdminAccountid;  //생성자 id

}
