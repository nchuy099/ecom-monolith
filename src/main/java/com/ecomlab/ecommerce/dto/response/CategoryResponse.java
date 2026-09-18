package com.ecomlab.ecommerce.dto.response;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
  private UUID id;
  private UUID parentId;
  private String name;
  private String description;
  private String slug;
  private String icon;
}
