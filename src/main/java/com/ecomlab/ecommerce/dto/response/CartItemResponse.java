package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
  private UUID id;
  private UUID variantId;
  private String sku;
  private String name;
  private BigDecimal price;
  private int quantity;
  private BigDecimal subtotal;

  public BigDecimal subtotal() {
    return subtotal;
  }
}
