package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.common.util.Haversine;
import com.ecomlab.ecommerce.dto.response.CheckoutResponse;
import com.ecomlab.ecommerce.dto.response.ShipmentPlanResponse;
import com.ecomlab.ecommerce.entity.OrderEntity;
import com.ecomlab.ecommerce.entity.UserAddressEntity;
import com.ecomlab.ecommerce.entity.WarehouseEntity;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CheckoutResponseBuilder {
  private CheckoutResponseBuilder() {}

  public static CheckoutResponse build(
      OrderEntity order,
      UserAddressEntity address,
      Map<WarehouseEntity, Map<UUID, Integer>> allocation) {
    List<ShipmentPlanResponse> shipmentPlans =
        allocation.entrySet().stream()
            .map(entry -> buildShipmentPlan(address, entry.getKey(), entry.getValue()))
            .toList();

    return CheckoutResponse.builder()
        .orderId(order.getId())
        .orderNumber(order.getOrderNumber())
        .totalAmount(order.getTotalAmount())
        .shipments(shipmentPlans)
        .build();
  }

  private static ShipmentPlanResponse buildShipmentPlan(
      UserAddressEntity address, WarehouseEntity warehouse, Map<UUID, Integer> lines) {
    return ShipmentPlanResponse.builder()
        .warehouseId(warehouse.getId())
        .warehouseName(warehouse.getName())
        .quantity(lines.values().stream().mapToInt(Integer::intValue).sum())
        .distanceKm(
            Haversine.kilometers(
                address.getLatitude(),
                address.getLongitude(),
                warehouse.getLatitude(),
                warehouse.getLongitude()))
        .build();
  }
}
