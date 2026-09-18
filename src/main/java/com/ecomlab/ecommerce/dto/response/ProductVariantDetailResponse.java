package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.Map;
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
public class ProductVariantDetailResponse {
  private UUID productId;
  private String productName;
  private UUID variantId;
  private String sku;
  private String variantName;
  private BigDecimal price;
  private long availableQuantity;
  private long reservedQuantity;
  private String imageUrl;
  private String description;
  private Map<String, String> attributes;
}
