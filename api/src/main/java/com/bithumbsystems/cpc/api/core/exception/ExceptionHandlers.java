package com.bithumbsystems.cpc.api.core.exception;

import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.core.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Mono<?>> serverExceptionHandler(Exception ex) {
    log.error(ex.getMessage(), ex);
    ErrorData errorData = new ErrorData(ErrorCode.UNKNOWN_ERROR);
    return ResponseEntity.internalServerError().body(Mono.just(new ErrorResponse(errorData)));
  }

  @ExceptionHandler(DuplicateKeyException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Mono<?>> serverDuplicateKeyExceptionHandler(DuplicateKeyException ex) {
    log.error(ex.getMessage(), ex);
    ErrorData errorData = new ErrorData(ErrorCode.DUPLICATE_KEY_ERROR);
    return ResponseEntity.internalServerError().body(Mono.just(new ErrorResponse(errorData)));
  }

  @ExceptionHandler(InvalidParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Mono<?>> serverExceptionHandler(InvalidParameterException ex) {
    log.error(ex.getMessage(), ex);
    ErrorData errorData = new ErrorData(ex.getErrorCode());
    return ResponseEntity.badRequest().body(Mono.just(new ErrorResponse(errorData)));
  }
}