package com.bithumbsystems.persistence.mongodb.file.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class File {
    @Id
    private String fileKey;

    private String fileName;
    private Date createdAt;
    private String createdId;
    private boolean delYn;
    private Date deletedAt;
    private String derId;

    @Builder
    public File(String fileKey, String fileName, Date createdAt, String createdId, boolean delYn) {
        this.fileKey = fileKey;
        this.fileName = fileName;
        this.createdAt = createdAt;
        this.createdId = createdId;
        this.delYn = delYn;
    }
}
