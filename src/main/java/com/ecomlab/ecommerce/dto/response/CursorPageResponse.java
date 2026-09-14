package com.ecomlab.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageResponse<T> {
  @JsonProperty("items")
  private List<T> content;

  private int size;

  @JsonProperty("hasMore")
  private boolean hasNext;

  private String nextCursor;
}
