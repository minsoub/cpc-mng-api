package com.bithumbsystems.cpc.api.core.exception;

import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
@Getter
public class InvalidTokenException extends RuntimeException {

  private final ErrorCode errorCode;

  public InvalidTokenException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
