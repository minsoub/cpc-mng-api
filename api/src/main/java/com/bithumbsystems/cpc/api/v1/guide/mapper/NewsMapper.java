package com.bithumbsystems.cpc.api.v1.guide.mapper;

import com.bithumbsystems.cpc.api.v1.guide.model.request.NewsRequest;
import com.bithumbsystems.cpc.api.v1.guide.model.response.NewsResponse;
import com.bithumbsystems.persistence.mongodb.guide.model.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NewsMapper {

  NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);

  NewsResponse toDto(News news);

  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  News toEntity(NewsRequest newsRequest);
}
