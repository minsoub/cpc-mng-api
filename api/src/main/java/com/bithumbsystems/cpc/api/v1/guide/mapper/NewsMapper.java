package com.bithumbsystems.cpc.api.v1.guide.mapper;

import com.bithumbsystems.cpc.api.v1.guide.model.request.NewsRequest;
import com.bithumbsystems.cpc.api.v1.guide.model.response.NewsResponse;
import com.bithumbsystems.persistence.mongodb.guide.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NewsMapper {

  NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);

  NewsResponse toDto(News news);

  News toEntity(NewsRequest newsRequest);
}
