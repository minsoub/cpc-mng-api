package com.bithumbsystems.persistence.mongodb.accesslog.model.entity;

import com.bithumbsystems.persistence.mongodb.accesslog.model.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document(collection = "primary_access_log")
@AllArgsConstructor
@Data
@Builder
public class AccessLog {
    @MongoId
    private String id;
    private String account_id;
    private String email;
    private String ip;
    private ActionType action_type;
    private String reason;
    private String description;
    private LocalDateTime create_date;
}
