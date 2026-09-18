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
public class OrderItemResponse {
  private UUID id;
  private UUID variantId;
  private String sku;
  private String name;
  private String productName;
  private String variantName;
  private BigDecimal unitPrice;
  private BigDecimal price;
  private int quantity;
  private BigDecimal subtotal;
  private String imageUrl;
}
