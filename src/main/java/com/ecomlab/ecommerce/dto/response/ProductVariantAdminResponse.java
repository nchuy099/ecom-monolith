package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantAdminResponse {
  private UUID id;
  private UUID productId;
  private String sku;
  private String name;
  private BigDecimal price;
  private boolean active;
}
