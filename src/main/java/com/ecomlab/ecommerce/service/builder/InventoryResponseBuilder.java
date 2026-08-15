package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.InventoryResponse;
import com.ecomlab.ecommerce.entity.InventoryEntity;

public final class InventoryResponseBuilder {
  private InventoryResponseBuilder() {}

  public static InventoryResponse build(InventoryEntity inventory) {
    return InventoryResponse.builder()
        .id(inventory.getId())
        .warehouseId(inventory.getWarehouse().getId())
        .variantId(inventory.getVariant().getId())
        .availableQuantity(inventory.getAvailableQuantity())
        .reservedQuantity(inventory.getReservedQuantity())
        .build();
  }
}
