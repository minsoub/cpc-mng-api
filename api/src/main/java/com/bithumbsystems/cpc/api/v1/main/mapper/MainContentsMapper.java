package com.bithumbsystems.cpc.api.v1.main.mapper;

import com.bithumbsystems.cpc.api.v1.main.model.response.MainContentsResponse;
import com.bithumbsystems.persistence.mongodb.main.model.entity.MainContents;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MainContentsMapper {

  MainContentsMapper INSTANCE = Mappers.getMapper(MainContentsMapper.class);

  MainContentsResponse toDto(MainContents mainContents);
}
