package com.bithumbsystems.cpc.api.core.model.response;

import lombok.Getter;
import org.joda.time.LocalDateTime;

@Getter
public class UploaderAnswer<T> {
  private final Boolean success;
  private String time;
  private T data;

  public UploaderAnswer(T data) {
    this.success = true;
    this.time = LocalDateTime.now().toString();
    this.data = data;
  }

  public UploaderAnswer() {
    this.success = true;
  }
}
