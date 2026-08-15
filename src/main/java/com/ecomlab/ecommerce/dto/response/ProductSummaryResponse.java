package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
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
public class ProductSummaryResponse {
  private UUID id;
  private String name;
  private UUID categoryId;
  private boolean active;
  private UUID variantId;
  private String sku;
  private String variantName;
  private BigDecimal price;
  private long availableQuantity;
  private long reservedQuantity;
}
