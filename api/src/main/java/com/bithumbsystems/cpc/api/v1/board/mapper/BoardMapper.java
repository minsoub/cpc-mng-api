package com.bithumbsystems.cpc.api.v1.board.mapper;

import com.bithumbsystems.cpc.api.v1.board.model.request.BoardRequest;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardListResponse;
import com.bithumbsystems.cpc.api.v1.board.model.response.BoardResponse;
import com.bithumbsystems.persistence.mongodb.account.model.entity.AdminAccount;
import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoardMapper {

  BoardMapper INSTANCE = Mappers.getMapper(BoardMapper.class);

  @Mapping(target = "id", source = "board.id")
  @Mapping(target = "isSetNotice", source = "board.isSetNotice")
  @Mapping(target = "category", source = "board.category")
  @Mapping(target = "title", source = "board.title")
  @Mapping(target = "contents", source = "board.contents")
  @Mapping(target = "tags", source = "board.tags")
  @Mapping(target = "thumbnail", source = "board.thumbnail")
  @Mapping(target = "description", source = "board.description")
  @Mapping(target = "contributor", source = "board.contributor")
  @Mapping(target = "createDate", source = "board.createDate")
  @Mapping(target = "createAccountId", source = "board.createAccountId")
  @Mapping(target = "createAccountName", expression = "java(account.getName() + '(' + account.getEmail() + ')')")
  BoardResponse toDto(Board board, AdminAccount account);

  @Mapping(target = "id", source = "board.id")
  @Mapping(target = "category", source = "board.category")
  @Mapping(target = "title", source = "board.title")
  @Mapping(target = "createDate", source = "board.createDate")
  @Mapping(target = "createAccountId", source = "board.createAccountId")
  @Mapping(target = "createAccountName", expression = "java(com.bithumbsystems.cpc.api.core.util.MaskingUtil.getNameMask(account.getName()) + '(' + com.bithumbsystems.cpc.api.core.util.MaskingUtil.getEmailMask(account.getEmail()) + ')')")
  BoardListResponse toDtoList(Board board, AdminAccount account);

  @Mapping(target = "readCount", ignore = true)
  @Mapping(target = "accountDocs", ignore = true)
  @Mapping(target = "createDate", ignore = true)
  @Mapping(target = "createAccountId", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  @Mapping(target = "updateAccountId", ignore = true)
  Board toEntity(BoardRequest boardRequest);
}
