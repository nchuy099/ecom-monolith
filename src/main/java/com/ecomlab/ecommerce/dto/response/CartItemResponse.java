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
  private UUID productId;
  private String productName;
  private String variantName;
  private String sku;
  private String name;
  private BigDecimal price;
  private int quantity;
  private BigDecimal lineTotal;
  private BigDecimal subtotal;
  private String imageUrl;
  private int availableQuantity;

  public BigDecimal subtotal() {
    return subtotal;
  }
}
