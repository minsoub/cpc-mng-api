package com.bithumbsystems.persistence.mongodb.account.model.entity;

import com.bithumbsystems.persistence.mongodb.account.model.enums.Status;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "admin_account")
@NoArgsConstructor
@Data
public class AdminAccount {
    @MongoId(value = FieldType.STRING, targetType = FieldType.STRING)
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private String phone;
    private String password;
    private String oldPassword;
    private String otpSecretKey;
    private LocalDateTime lastLoginDate;
    private Status status;
    private Boolean isUse;
    private LocalDateTime lastPasswordUpdateDate;

    private LocalDateTime createDate;
    private String createAdminAccountId;
    private LocalDateTime updateDate;
    private String updateAdminAccountId;
    public void setStatusByIsUse(Boolean isUse){
        if (isUse)
            this.status = Status.INIT_REQUEST;
        else
            this.status = Status.DENY_ACCESS;
    }
}