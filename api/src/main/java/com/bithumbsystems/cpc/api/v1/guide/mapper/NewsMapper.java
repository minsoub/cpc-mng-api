package com.bithumbsystems.cpc.api.v1.guide.mapper;

import com.bithumbsystems.cpc.api.v1.guide.model.request.NewsRequest;
import com.bithumbsystems.cpc.api.v1.guide.model.response.NewsListResponse;
import com.bithumbsystems.cpc.api.v1.guide.model.response.NewsResponse;
import com.bithumbsystems.persistence.mongodb.account.model.entity.AdminAccount;
import com.bithumbsystems.persistence.mongodb.guide.model.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NewsMapper {

  NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);

  NewsResponse toDto(News news);

  @Mapping(target = "id", source = "news.id")
  @Mapping(target = "newspaper", source = "news.newspaper")
  @Mapping(target = "title", source = "news.title")
  @Mapping(target = "linkUrl", source = "news.linkUrl")
  @Mapping(target = "postingDate", source = "news.postingDate")
  @Mapping(target = "createDate", source = "news.createDate")
  @Mapping(target = "createAccountId", source = "news.createAccountId")
  @Mapping(target = "createAccountName", expression = "java(account.getName() + '(' + account.getEmail() + ')')")
  NewsListResponse toDtoList(News news, AdminAccount account);

  @Mapping(target = "readCount", ignore = true)
  @Mapping(target = "accountDocs", ignore = true)
  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "createAccountId", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  @Mapping(target = "updateAccountId", ignore = true)
  News toEntity(NewsRequest newsRequest);
}
