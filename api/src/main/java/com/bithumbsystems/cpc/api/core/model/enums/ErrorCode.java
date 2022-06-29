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
  FAIL_SEND_MAIL("M411","FAIL_SEND_MAIL");

  private final String code;
  private final String message;
}
