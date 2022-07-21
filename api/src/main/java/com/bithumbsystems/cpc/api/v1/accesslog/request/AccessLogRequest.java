package com.bithumbsystems.cpc.api.v1.accesslog.request;

import com.bithumbsystems.persistence.mongodb.accesslog.model.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessLogRequest {
    private String accountId;
    private String email;
    private String ip;
    private ActionType actionType;
    private String reason;
    private String description;
}
