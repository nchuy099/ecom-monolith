package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDetailProjection(
    UUID productId,
    String productName,
    UUID variantId,
    String sku,
    String variantName,
    BigDecimal price,
    long availableQuantity,
    long reservedQuantity,
    String imageUrl,
    String description,
    String attributesJson) {}
