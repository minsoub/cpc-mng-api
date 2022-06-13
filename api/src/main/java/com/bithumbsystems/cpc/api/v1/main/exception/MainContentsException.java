package com.bithumbsystems.cpc.api.v1.main.exception;

import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
public class MainContentsException extends RuntimeException{

  private final ErrorCode errorCode;

  public MainContentsException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}