package com.bithumbsystems.cpc.api.v1.education.mapper;

import com.bithumbsystems.cpc.api.v1.education.model.response.EducationResponse;
import com.bithumbsystems.persistence.mongodb.education.model.entity.Education;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EducationMapper {

    EducationMapper INSTANCE = Mappers.getMapper(EducationMapper.class);

    EducationResponse educationResponse(Education education);
}
