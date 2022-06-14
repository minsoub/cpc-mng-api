package com.bithumbsystems.cpc.api.v1.main.model.enums;

import com.bithumbsystems.cpc.api.core.model.enums.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BulletinBoardType implements EnumMapperType {

  CPC_VIRTUAL_ASSET("가상자산의 기초"),
  CPC_EXPERT_COLUMN("전문가 칼럼"),
  CPC_OPINION_COLUMN("오피니언 칼럼"),
  CPC_BITHUMN_RESEARCH("빗썸경제연구소");

  private final String title;

  @Override
  public String getCode() {
    return name();
  }

  public static String getTitle(String code) {
    for (BulletinBoardType bulletinBoardType : BulletinBoardType.values()) {
      if (code.equals(bulletinBoardType.getCode())) {
        return bulletinBoardType.getTitle();
      }
    }
    return null;
  }
}