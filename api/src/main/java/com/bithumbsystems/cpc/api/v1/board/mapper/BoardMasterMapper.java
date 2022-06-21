package com.bithumbsystems.cpc.api.v1.board.mapper;

import com.bithumbsystems.cpc.api.v1.board.model.request.BoardMasterRequest;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardMasterListResponse;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardMasterResponse;
import com.bithumbsystems.persistence.mongodb.board.model.entity.BoardMaster;
import com.bithumbsystems.persistence.mongodb.board.model.entity.BoardMaster.Auth;
import com.bithumbsystems.persistence.mongodb.board.model.entity.BoardMaster.Sns;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoardMasterMapper {

  BoardMasterMapper INSTANCE = Mappers.getMapper(BoardMasterMapper.class);

  BoardMasterResponse toDto(BoardMaster boardMaster);

  @Mapping(expression = "java(com.bithumbsystems.cpc.api.v1.board.model.enums.BoardType.getTitle(boardMaster.getType()))", target = "typeName")
  @Mapping(target = "siteName", ignore = true) //TODO: 사이트명 변환 필요
  BoardMasterListResponse toDtoForList(BoardMaster boardMaster);


  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "createAccountId", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  @Mapping(target = "updateAccountId", ignore = true)
  BoardMaster toEntity(BoardMasterRequest boardMasterRequest, Sns snsShare, Auth auth);
}