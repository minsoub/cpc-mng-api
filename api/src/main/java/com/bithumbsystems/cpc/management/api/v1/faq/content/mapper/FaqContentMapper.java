package com.bithumbsystems.cpc.management.api.v1.faq.content.mapper;

import com.bithumbsystems.cpc.management.api.v1.faq.content.model.response.FaqContentResponse;
import com.bithumbsystems.persistence.mongodb.faq.content.model.entity.FaqContent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FaqContentMapper {

    FaqContentMapper INSTANCE = Mappers.getMapper(FaqContentMapper.class);

    FaqContentResponse faqContentRespone(FaqContent faqContent);
}
