package com.bithumbsystems.cpc.api.core.exception;

import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import com.bithumbsystems.cpc.api.core.model.response.ErrorResponse;
import com.bithumbsystems.cpc.api.v1.board.exception.BoardException;
import com.bithumbsystems.cpc.api.v1.guide.exception.NewsException;
import com.bithumbsystems.cpc.api.v1.protection.exception.FraudReportException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

  @ExceptionHandler(BoardException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Mono<?>> serverBoardExceptionHandler(BoardException ex) {
    log.error(ex.getMessage(), ex);
    ErrorResponse errorResponse = new ErrorResponse(new ErrorData(ex.getErrorCode()));
    return ResponseEntity.internalServerError().body(Mono.just(errorResponse));
  }

  @ExceptionHandler(NewsException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Mono<?>> serverNewsExceptionHandler(NewsException ex) {
    log.error(ex.getMessage(), ex);
    ErrorResponse errorResponse = new ErrorResponse(new ErrorData(ex.getErrorCode()));
    return ResponseEntity.internalServerError().body(Mono.just(errorResponse));
  }

  @ExceptionHandler(FraudReportException.class)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Mono<?>> serverFraudReportExceptionHandler(FraudReportException ex) {
    log.error(ex.getMessage(), ex);
    ErrorResponse errorResponse = new ErrorResponse(new ErrorData(ex.getErrorCode()));
    return ResponseEntity.internalServerError().body(Mono.just(errorResponse));
  }
}