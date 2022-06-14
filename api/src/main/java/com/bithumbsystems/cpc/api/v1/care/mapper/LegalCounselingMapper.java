package com.bithumbsystems.cpc.api.v1.care.mapper;

import com.bithumbsystems.cpc.api.v1.care.model.request.LegalCounselingRequest;
import com.bithumbsystems.cpc.api.v1.care.model.response.LegalCounselingResponse;
import com.bithumbsystems.persistence.mongodb.care.model.entity.LegalCounseling;
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
  LegalCounseling toEntity(LegalCounselingRequest legalCounselingRequest);

  LegalCounselingResponse toDto(LegalCounseling legalCounseling);
}