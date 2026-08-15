package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import com.ecomlab.ecommerce.dto.response.ShipmentSummaryResponse;
import com.ecomlab.ecommerce.entity.ShipmentEntity;

public final class ShipmentResponseBuilder {
  private ShipmentResponseBuilder() {}

  public static ShipmentResponse build(ShipmentEntity shipment) {
    return ShipmentResponse.builder()
        .id(shipment.getId())
        .orderId(shipment.getOrder().getId())
        .warehouseId(shipment.getWarehouse().getId())
        .shipperId(shipment.getShipper() == null ? null : shipment.getShipper().getId())
        .status(shipment.getStatus().name())
        .build();
  }

  public static ShipmentSummaryResponse summary(ShipmentEntity shipment) {
    return ShipmentSummaryResponse.builder()
        .id(shipment.getId())
        .status(shipment.getStatus().name())
        .warehouseName(shipment.getWarehouse().getName())
        .warehouseAddress(shipment.getWarehouse().getAddressLine())
        .build();
  }
}
