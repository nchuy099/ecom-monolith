package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.common.util.Haversine;
import com.ecomlab.ecommerce.dto.response.CheckoutItemAvailabilityResponse;
import com.ecomlab.ecommerce.dto.response.CheckoutResponse;
import com.ecomlab.ecommerce.dto.response.ShipmentPlanResponse;
import com.ecomlab.ecommerce.entity.CartItemEntity;
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
      Map<WarehouseEntity, Map<UUID, Integer>> allocation,
      List<CheckoutItemAvailabilityResponse> items) {
    List<ShipmentPlanResponse> shipmentPlans =
        allocation.entrySet().stream()
            .map(entry -> buildShipmentPlan(address, entry.getKey(), entry.getValue()))
            .toList();

    return CheckoutResponse.builder()
        .orderId(order.getId())
        .orderNumber(order.getOrderNumber())
        .totalAmount(order.getTotalAmount())
        .shipments(shipmentPlans)
        .items(items)
        .build();
  }

  public static CheckoutResponse preview(
      UserAddressEntity address,
      Map<WarehouseEntity, Map<UUID, Integer>> allocation,
      List<CheckoutItemAvailabilityResponse> items) {
    return CheckoutResponse.builder()
        .shipments(
            allocation.entrySet().stream()
                .map(entry -> buildShipmentPlan(address, entry.getKey(), entry.getValue()))
                .toList())
        .items(items)
        .build();
  }

  public static List<CheckoutItemAvailabilityResponse> itemAvailability(
      List<CartItemEntity> cartItems, Map<UUID, Integer> availableQuantities) {
    return cartItems.stream()
        .map(
            item ->
                itemAvailability(
                    item, availableQuantities.getOrDefault(item.getVariant().getId(), 0)))
        .toList();
  }

  private static CheckoutItemAvailabilityResponse itemAvailability(
      CartItemEntity item, int availableQuantity) {
    int requestedQuantity = item.getQuantity();
    int missingQuantity = Math.max(requestedQuantity - availableQuantity, 0);

    return CheckoutItemAvailabilityResponse.builder()
        .variantId(item.getVariant().getId())
        .sku(item.getVariant().getSku())
        .productName(item.getVariant().getProduct().getName())
        .variantName(item.getVariant().getName())
        .requestedQuantity(requestedQuantity)
        .availableInSelectedZone(availableQuantity)
        .missingQuantity(missingQuantity)
        .available(missingQuantity == 0)
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
