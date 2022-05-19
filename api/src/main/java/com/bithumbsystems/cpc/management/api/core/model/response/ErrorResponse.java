package com.bithumbsystems.cpc.management.api.core.model.response;


import com.bithumbsystems.cpc.management.api.core.exception.ErrorData;
import com.bithumbsystems.cpc.management.api.core.model.enums.ReturnCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

  private final ReturnCode result;
  private final ErrorData data;

  public ErrorResponse(ErrorData data) {
    this.result = ReturnCode.ERROR;
    this.data = data;
  }
}