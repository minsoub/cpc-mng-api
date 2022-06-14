package com.bithumbsystems.cpc.api.v1.protection.model.enums;

import com.bithumbsystems.cpc.api.core.model.enums.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

  public static String getTitle(String code) {
    for (Status status : Status.values()) {
      if (code.equals(status.getCode())) {
        return status.getTitle();
      }
    }
    return null;
  }
}
