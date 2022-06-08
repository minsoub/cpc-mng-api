package com.bithumbsystems.cpc.api.v1.board.model.enums;

import com.bithumbsystems.cpc.api.core.model.enums.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum BoardType implements EnumMapperType {

  LIST("목록형"),
  CARD("카드형"),
  NOTICE("공지형");

  private final String title;

  @Override
  public String getCode() {
    return name();
  }

  public static @Nullable String getTitle(String code) {
    for (BoardType boardType : BoardType.values()) {
      if (code.equals(boardType.getCode())) {
        return boardType.getTitle();
      }
    }
    return null;
  }
}
