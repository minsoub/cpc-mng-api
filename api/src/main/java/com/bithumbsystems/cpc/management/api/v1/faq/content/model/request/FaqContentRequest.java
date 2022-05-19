package com.bithumbsystems.cpc.management.api.v1.faq.content.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaqContentRequest {

    private String id;
    private String userId;      //사용자 id
    private String order;       //노출순서
    private String category;    //카테고리
    private String title;       //제목
    private String content;     //내용
    private Boolean useYn;       //사용여부
    private String costumer;    //등록자
    private String language;    //언어

    private LocalDateTime createDate; //생성날짜
    private String createAdminAccountid;  //생성자 id

}
