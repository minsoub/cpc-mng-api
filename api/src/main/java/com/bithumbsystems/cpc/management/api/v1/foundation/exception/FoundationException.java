package com.bithumbsystems.cpc.management.api.v1.foundation.exception;

import com.bithumbsystems.cpc.management.api.core.model.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FoundationException extends RuntimeException {
  public FoundationException(ErrorCode errorCode) {
    super(String.valueOf(errorCode));
  }
}