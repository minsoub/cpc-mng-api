package com.bithumbsystems.cpc.api.core.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  UNKNOWN_ERROR("F001", "error"),
  INVALID_TOKEN("F401","Invalid token"),
  NOT_EXIST_ACCOUNT("F404","NOT_EXIST_ACCOUNT"),
  INVALID_FILE("F002","file is invalid"),
  FAIL_SAVE_FILE("F003","file save fail"),
  NOT_FOUND_CONTENT("F004","not found content"),
  FAIL_UPDATE_CONTENT("F005","cannot update content"),
  FAIL_CREATE_CONTENT("F006","cannot create content"),
  FAIL_DELETE_CONTENT("F007","cannot delete content"),
  DUPLICATE_KEY_ERROR("F008", "duplicate key error"),
  TIMEOUT_ERROR("F009", "timeout error"),
  EXCEL_DOWNLOAD_ERROR("F011", "excel download error"),
  FAIL_SEND_MAIL("M411","FAIL_SEND_MAIL"),

  INVALID_EMAIL_FORMAT("F012", "잘못된 이메일 형식입니다. 이메일 주소를 확인해 주세요."),
  INVALID_NAME_FORMAT("F013", "이름에는 특수문자를 포함할 수 없습니다."),
  INVALID_PHONE_FORMAT("F014", "잘못된 휴대폰번호 형식입니다. 휴대폰번호를 확인해 주세요."),
  NOT_ALLOWED_FILE_EXT("F015", "허용되지 않은 파일 확장자입니다. 파일을 확인해 주세요."),
  NOT_ALLOWED_FILE_SIZE("F016", "첨부가능한 파일 크기를 초과했습니다.");

  private final String code;
  private final String message;
}
