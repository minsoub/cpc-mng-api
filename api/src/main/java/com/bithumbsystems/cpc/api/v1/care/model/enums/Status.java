package com.bithumbsystems.cpc.api.v1.care.model.enums;

import com.bithumbsystems.cpc.api.core.model.enums.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum Status implements EnumMapperType {

  REGISTER("접수"),
  REQUEST("답변요청"),
  COMPLETE("답변완료");

  private final String title;

  @Override
  public String getCode() {
    return name();
  }

  public static @Nullable String getTitle(String code) {
    for (Status status : Status.values()) {
      if (code.equals(status.getCode())) {
        return status.getTitle();
      }
    }
    return null;
  }
}
