package com.bithumbsystems.cpc.api.v1.guide.exception;

import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
public class NewsException extends RuntimeException{

  private final ErrorCode errorCode;

  public NewsException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}