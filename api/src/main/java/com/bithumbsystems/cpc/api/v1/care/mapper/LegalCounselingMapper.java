package com.bithumbsystems.cpc.api.v1.care.mapper;

import com.bithumbsystems.cpc.api.v1.care.model.request.LegalCounselingRequest;
import com.bithumbsystems.cpc.api.v1.care.model.response.LegalCounselingResponse;
import com.bithumbsystems.persistence.mongodb.care.model.entity.LegalCounseling;
import com.bithumbsystems.persistence.mongodb.common.model.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LegalCounselingMapper {

  LegalCounselingMapper INSTANCE = Mappers.getMapper(LegalCounselingMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "answer", ignore = true)
  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "createAccountId", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  @Mapping(target = "updateAccountId", ignore = true)
  @Mapping(target = "fileDocs", ignore = true)
  LegalCounseling toEntity(LegalCounselingRequest legalCounselingRequest);

  @Mapping(target = "id", source = "legalCounseling.id")
  @Mapping(target = "status", source = "legalCounseling.status")
  @Mapping(target = "name", source = "legalCounseling.name")
  @Mapping(target = "email", source = "legalCounseling.email")
  @Mapping(target = "cellPhone", source = "legalCounseling.cellPhone")
  @Mapping(target = "contents", source = "legalCounseling.contents")
  @Mapping(target = "entrustPrivacy", source = "legalCounseling.entrustPrivacy")
  @Mapping(target = "termsPrivacy", source = "legalCounseling.termsPrivacy")
  @Mapping(target = "answerToContacts", source = "legalCounseling.answerToContacts")
  @Mapping(target = "answer", source = "legalCounseling.answer")
  @Mapping(target = "sendToEmail", source = "legalCounseling.sendToEmail")
  @Mapping(target = "attachFileId", source = "legalCounseling.attachFileId")
  @Mapping(target = "attachFileName", source = "file.fileName")
  @Mapping(target = "createDate", source = "legalCounseling.createDate")
  LegalCounselingResponse toDto(LegalCounseling legalCounseling, File file);
}