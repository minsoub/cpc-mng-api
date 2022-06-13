package com.bithumbsystems.cpc.api.v1.main.mapper;

import com.bithumbsystems.cpc.api.v1.main.model.request.MainContentsRequest;
import com.bithumbsystems.cpc.api.v1.main.model.response.MainContentsResponse;
import com.bithumbsystems.persistence.mongodb.main.model.entity.MainContents;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MainContentsMapper {

  MainContentsMapper INSTANCE = Mappers.getMapper(MainContentsMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "createAccountId", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  @Mapping(target = "updateAccountId", ignore = true)
  MainContents toEntity(MainContentsRequest mainContentsRequest);
}
