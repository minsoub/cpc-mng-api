package com.bithumbsystems.cpc.api.core.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Value;

@Value
public class PageSupport<T> {

  public static final String FIRST_PAGE_NUM = "1";
  public static final String DEFAULT_PAGE_SIZE = "12";

  List<T> contents;
  int pageNumber;
  int pageSize;
  long totalElements;

  @JsonProperty
  public long totalPages() {
    return pageSize > 0 ? (totalElements - 1) / pageSize + 1 : 0;
  }

  @JsonProperty
  public boolean first() {
    return pageNumber == Integer.parseInt(FIRST_PAGE_NUM);
  }

  @JsonProperty
  public boolean last() {
    return (pageNumber) * pageSize >= totalElements;
  }
}
