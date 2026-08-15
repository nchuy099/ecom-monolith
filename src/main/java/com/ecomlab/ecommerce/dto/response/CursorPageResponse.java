package com.ecomlab.ecommerce.dto.response;

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
  private List<T> content;
  private int size;
  private boolean hasNext;
  private String nextCursor;
}
