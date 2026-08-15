package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.WarehouseResponse;
import com.ecomlab.ecommerce.entity.WarehouseEntity;

public final class WarehouseResponseBuilder {
  private WarehouseResponseBuilder() {}

  public static WarehouseResponse build(WarehouseEntity warehouse) {
    return WarehouseResponse.builder()
        .id(warehouse.getId())
        .code(warehouse.getCode())
        .name(warehouse.getName())
        .addressLine(warehouse.getAddressLine())
        .latitude(warehouse.getLatitude())
        .longitude(warehouse.getLongitude())
        .active(warehouse.isActive())
        .build();
  }
}
