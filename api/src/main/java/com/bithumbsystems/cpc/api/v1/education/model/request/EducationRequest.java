package com.bithumbsystems.cpc.api.v1.education.model.request;

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
@Schema(description = "신청자 답변정보")
public class EducationRequest {
    @Schema(description = "id")
    private String id;
    @Schema(description = "메일전송 여부")
    private Boolean isEmail;
    @Schema(description = "답변")
    private String answer;
    @Schema(description = "마스킹여부")
    private Boolean isMasking;
}
