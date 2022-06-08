package com.bithumbsystems.cpc.api.v1.board.model.enums;

import com.bithumbsystems.cpc.api.core.model.enums.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum PaginationType implements EnumMapperType {

  BUTTON("버튼방식"),
  SCROLL("스크롤방식");

  private final String title;

  @Override
  public String getCode() {
    return name();
  }

  public static @Nullable String getTitle(String code) {
    for (PaginationType boardType : PaginationType.values()) {
      if (code.equals(boardType.getCode())) {
        return boardType.getTitle();
      }
    }
    return null;
  }
}
