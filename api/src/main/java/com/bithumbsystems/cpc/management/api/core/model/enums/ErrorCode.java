package com.bithumbsystems.cpc.management.api.core.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  UNKNOWN_ERROR("error"),

  INVALID_FILE("file is invalid"),
  FAIL_SAVE_FILE("file save fail");

  private final String message;
}
