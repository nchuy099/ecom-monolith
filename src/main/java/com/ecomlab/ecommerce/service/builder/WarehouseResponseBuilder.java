package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.WarehouseResponse;
import com.ecomlab.ecommerce.entity.WarehouseEntity;
import com.ecomlab.ecommerce.entity.WarehouseShippingZoneEntity;
import java.util.List;

public final class WarehouseResponseBuilder {
  private WarehouseResponseBuilder() {}

  public static WarehouseResponse build(WarehouseEntity warehouse) {
    return build(warehouse, List.of());
  }

  public static WarehouseResponse build(
      WarehouseEntity warehouse, List<WarehouseShippingZoneEntity> shippingZones) {
    return WarehouseResponse.builder()
        .id(warehouse.getId())
        .code(warehouse.getCode())
        .name(warehouse.getName())
        .addressLine(warehouse.getAddressLine())
        .priorityArea(warehouse.getPriorityArea())
        .latitude(warehouse.getLatitude())
        .longitude(warehouse.getLongitude())
        .active(warehouse.isActive())
        .shippingZones(shippingZones.stream().map(ShippingZoneResponseBuilder::build).toList())
        .build();
  }
}
