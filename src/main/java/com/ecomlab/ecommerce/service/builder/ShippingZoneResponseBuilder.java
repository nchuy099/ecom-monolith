package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.ShippingZoneResponse;
import com.ecomlab.ecommerce.dto.response.WarehouseShippingZoneResponse;
import com.ecomlab.ecommerce.entity.ShippingZoneEntity;
import com.ecomlab.ecommerce.entity.WarehouseShippingZoneEntity;
import java.util.Arrays;
import java.util.List;

public final class ShippingZoneResponseBuilder {
  private ShippingZoneResponseBuilder() {}

  public static ShippingZoneResponse build(ShippingZoneEntity shippingZone) {
    return ShippingZoneResponse.builder()
        .id(shippingZone.getId())
        .code(shippingZone.getCode())
        .name(shippingZone.getName())
        .matchedCities(cities(shippingZone.getMatchedCities()))
        .active(shippingZone.isActive())
        .build();
  }

  public static WarehouseShippingZoneResponse build(WarehouseShippingZoneEntity mapping) {
    return WarehouseShippingZoneResponse.builder()
        .id(mapping.getId())
        .shippingZoneId(mapping.getShippingZone().getId())
        .shippingZoneCode(mapping.getShippingZone().getCode())
        .shippingZoneName(mapping.getShippingZone().getName())
        .priority(mapping.getPriority())
        .active(mapping.isActive())
        .build();
  }

  private static List<String> cities(String matchedCities) {
    if (matchedCities == null || matchedCities.isBlank()) {
      return List.of();
    }

    return Arrays.stream(matchedCities.split(","))
        .map(String::trim)
        .filter(city -> !city.isBlank())
        .toList();
  }
}
