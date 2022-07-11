package com.bithumbsystems.cpc.api.v1.board.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UploaderData {
  private List<String> messages;
  private List<String> files;
  @JsonProperty(value = "isImages") private List<Boolean> isImages;
  private String path;
  private String baseurl;
  private String newfilename;
}
