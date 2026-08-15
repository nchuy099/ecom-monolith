package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductListProjection(
    UUID id,
    String name,
    UUID categoryId,
    boolean active,
    UUID variantId,
    String sku,
    String variantName,
    BigDecimal price,
    long availableQuantity,
    long reservedQuantity) {}
