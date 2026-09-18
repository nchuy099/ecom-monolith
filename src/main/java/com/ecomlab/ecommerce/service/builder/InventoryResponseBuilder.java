package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.InventoryResponse;
import com.ecomlab.ecommerce.entity.InventoryEntity;

public final class InventoryResponseBuilder {
  private InventoryResponseBuilder() {}

  public static InventoryResponse build(InventoryEntity inventory) {
    return InventoryResponse.builder()
        .id(inventory.getId())
        .warehouseId(inventory.getWarehouse().getId())
        .warehouseName(inventory.getWarehouse().getName())
        .warehousePriorityArea(inventory.getWarehouse().getPriorityArea())
        .variantId(inventory.getVariant().getId())
        .sku(inventory.getVariant().getSku())
        .productName(inventory.getVariant().getProduct().getName())
        .availableQuantity(inventory.getAvailableQuantity())
        .reservedQuantity(inventory.getReservedQuantity())
        .build();
  }

  public static InventoryResponse build(
      InventoryEntity inventory, boolean deliverableToSelectedAddress) {
    InventoryResponse response = build(inventory);
    response.setDeliverableToSelectedAddress(deliverableToSelectedAddress);
    return response;
  }
}
