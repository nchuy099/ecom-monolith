package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductListProjection(
    UUID id,
    String name,
    UUID categoryId,
    String categoryName,
    boolean active,
    UUID variantId,
    String sku,
    String variantName,
    BigDecimal price,
    long availableQuantity,
    long reservedQuantity,
    String imageUrl,
    BigDecimal rating,
    Integer reviewCount,
    String badge) {
  public ProductListProjection(
      UUID id,
      String name,
      UUID categoryId,
      boolean active,
      UUID variantId,
      String sku,
      String variantName,
      BigDecimal price,
      long availableQuantity,
      long reservedQuantity) {
    this(
        id,
        name,
        categoryId,
        null,
        active,
        variantId,
        sku,
        variantName,
        price,
        availableQuantity,
        reservedQuantity,
        null,
        null,
        null,
        null);
  }
}
