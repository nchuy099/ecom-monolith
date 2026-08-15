package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.OrderItemResponse;
import com.ecomlab.ecommerce.dto.response.OrderResponse;
import com.ecomlab.ecommerce.entity.OrderEntity;

public final class OrderResponseBuilder {
  private OrderResponseBuilder() {}

  public static OrderResponse build(OrderEntity order) {
    return OrderResponse.builder()
        .id(order.getId())
        .orderNumber(order.getOrderNumber())
        .status(order.getStatus().name())
        .totalAmount(order.getTotalAmount())
        .city(order.getCity())
        .items(
            order.getItems().stream()
                .map(
                    item ->
                        OrderItemResponse.builder()
                            .id(item.getId())
                            .variantId(item.getVariant().getId())
                            .sku(item.getSkuSnapshot())
                            .name(item.getNameSnapshot())
                            .unitPrice(item.getUnitPrice())
                            .quantity(item.getQuantity())
                            .build())
                .toList())
        .build();
  }
}
