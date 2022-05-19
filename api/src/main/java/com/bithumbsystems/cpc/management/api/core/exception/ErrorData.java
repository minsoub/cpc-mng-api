package com.bithumbsystems.cpc.management.api.core.exception;

import com.bithumbsystems.cpc.management.api.core.model.enums.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorData {
  private final String error;
  private final String message;

  public ErrorData(ErrorCode errorCode) {
    this.error = errorCode.name();
    this.message = errorCode.getMessage();
  }
}