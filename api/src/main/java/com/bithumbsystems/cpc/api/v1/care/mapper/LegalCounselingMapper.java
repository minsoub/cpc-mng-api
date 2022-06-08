package com.bithumbsystems.cpc.api.v1.care.mapper;

import com.bithumbsystems.cpc.api.v1.care.model.request.LegalCounselingRequest;
import com.bithumbsystems.persistence.mongodb.care.entity.LegalCounseling;
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
  @Mapping(target = "updateDate", ignore = true)
  LegalCounseling toEntity(LegalCounselingRequest legalCounselingRequest);
}