package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.OrderItemResponse;
import com.ecomlab.ecommerce.dto.response.OrderResponse;
import com.ecomlab.ecommerce.entity.OrderEntity;
import com.ecomlab.ecommerce.entity.OrderItemEntity;
import java.math.BigDecimal;

public final class OrderResponseBuilder {
  private OrderResponseBuilder() {}

  public static OrderResponse build(OrderEntity order) {
    return OrderResponse.builder()
        .id(order.getId())
        .orderNumber(order.getOrderNumber())
        .status(frontendStatus(order.getStatus().name()))
        .totalAmount(order.getTotalAmount())
        .city(order.getCity())
        .recipientName(order.getRecipientName())
        .phone(order.getPhone())
        .addressLine(order.getAddressLine())
        .items(order.getItems().stream().map(OrderResponseBuilder::item).toList())
        .createdAt(order.getCreatedAt())
        .build();
  }

  private static OrderItemResponse item(OrderItemEntity item) {
    BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    String productName =
        item.getVariant() == null
            ? item.getNameSnapshot()
            : item.getVariant().getProduct().getName();

    return OrderItemResponse.builder()
        .id(item.getId())
        .variantId(item.getVariant().getId())
        .sku(item.getSkuSnapshot())
        .name(item.getNameSnapshot())
        .productName(productName)
        .variantName(item.getNameSnapshot())
        .unitPrice(item.getUnitPrice())
        .price(item.getUnitPrice())
        .quantity(item.getQuantity())
        .subtotal(subtotal)
        .imageUrl(item.getVariant().getImageUrl())
        .build();
  }

  private static String frontendStatus(String status) {
    return switch (status) {
      case "PENDING_PAYMENT" -> "PENDING";
      case "COMPLETED" -> "DELIVERED";
      default -> status;
    };
  }
}
