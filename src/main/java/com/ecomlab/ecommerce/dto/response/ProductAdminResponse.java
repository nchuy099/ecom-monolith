package com.ecomlab.ecommerce.dto.response;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminResponse {
  private UUID id;
  private UUID categoryId;
  private String name;
  private String description;
  private boolean active;
}
