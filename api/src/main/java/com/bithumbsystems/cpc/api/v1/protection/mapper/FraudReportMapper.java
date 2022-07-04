package com.bithumbsystems.cpc.api.v1.protection.mapper;

import com.bithumbsystems.cpc.api.v1.protection.model.request.FraudReportRequest;
import com.bithumbsystems.cpc.api.v1.protection.model.response.FraudReportResponse;
import com.bithumbsystems.persistence.mongodb.common.model.entity.File;
import com.bithumbsystems.persistence.mongodb.protection.model.entity.FraudReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FraudReportMapper {

  FraudReportMapper INSTANCE = Mappers.getMapper(FraudReportMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "attachFileId", ignore = true)
  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "createAccountId", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  @Mapping(target = "updateAccountId", ignore = true)
  @Mapping(target = "fileDocs", ignore = true)
  FraudReport toEntity(FraudReportRequest fraudReportRequest);

  @Mapping(target = "id", source = "fraudReport.id")
  @Mapping(target = "status", source = "fraudReport.status")
  @Mapping(target = "email", source = "fraudReport.email")
  @Mapping(target = "title", source = "fraudReport.title")
  @Mapping(target = "contents", source = "fraudReport.contents")
  @Mapping(target = "termsPrivacy", source = "fraudReport.termsPrivacy")
  @Mapping(target = "answerToContacts", source = "fraudReport.answerToContacts")
  @Mapping(target = "answer", source = "fraudReport.answer")
  @Mapping(target = "sendToEmail", source = "fraudReport.sendToEmail")
  @Mapping(target = "attachFileId", source = "fraudReport.attachFileId")
  @Mapping(target = "attachFileName", source = "file.fileName")
  @Mapping(target = "createDate", source = "fraudReport.createDate")
  FraudReportResponse toDto(FraudReport fraudReport, File file);
}
