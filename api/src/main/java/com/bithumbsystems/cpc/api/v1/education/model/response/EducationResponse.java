package com.bithumbsystems.cpc.api.v1.education.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Schema(description = "신청자 상세정보")
public class EducationResponse {
    @Schema(description = "ID")
    private String id;
    @Schema(description = "이름")
    private String name;
    @Schema(description = "이메일주소")
    private String email;
    @Schema(description = "휴대폰번호")
    private String cellPhone;
    @Schema(description = "신청내용")
    private String content;
    @Schema(description = "교육희망일")
    private LocalDateTime desireDate;
    @Schema(description = "답변여부")
    private Boolean isAnswerComplete;
    //@Schema(description = "개인정보 위탁 동의")
    //private Boolean isConsignmentAgreement;
    @Schema(description = "개인정보 수집 및 이용동의")
    private Boolean isUseAgreement;
    @Schema(description = "메일전송여부")
    private Boolean isEmail;
    @Schema(description = "답변")
    private String answer;
    @Schema(description = "생성일자")
    private LocalDateTime createDate;
    @Schema(description = "답변일자")
    private LocalDateTime updateDate;
    @Schema(description = "답변자 ID")
    private String updateAccountId;
}
