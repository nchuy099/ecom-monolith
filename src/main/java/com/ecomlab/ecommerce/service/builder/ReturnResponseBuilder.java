package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.ReturnResponse;
import com.ecomlab.ecommerce.dto.response.ReturnItemResponse;
import com.ecomlab.ecommerce.entity.ReturnItemEntity;
import com.ecomlab.ecommerce.entity.ReturnRequestEntity;
import java.math.BigDecimal;
import java.util.List;

public final class ReturnResponseBuilder {
  private ReturnResponseBuilder() {}

  public static ReturnResponse build(
      ReturnRequestEntity value, List<com.ecomlab.ecommerce.entity.ShipmentEntity> shipments) {
    return ReturnResponse.builder()
        .id(value.getId())
        .orderId(value.getOrder().getId())
        .status(value.getStatus().name())
        .reason(value.getReason())
        .decisionNote(value.getDecisionNote())
        .createdAt(value.getCreatedAt())
        .updatedAt(value.getUpdatedAt())
        .refundableAmount(
            value.getItems().stream()
                .map(
                    item ->
                        item.getOrderItem()
                            .getUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getReceivedQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add))
        .items(value.getItems().stream().map(ReturnResponseBuilder::item).toList())
        .shipments(shipments.stream().map(ShipmentResponseBuilder::build).toList())
        .build();
  }

  private static ReturnItemResponse item(ReturnItemEntity value) {
    return ReturnItemResponse.builder()
        .id(value.getId())
        .orderItemId(value.getOrderItem().getId())
        .sku(value.getOrderItem().getSkuSnapshot())
        .name(value.getOrderItem().getNameSnapshot())
        .unitPrice(value.getOrderItem().getUnitPrice())
        .requestedQuantity(value.getQuantity())
        .receivedQuantity(value.getReceivedQuantity())
        .restockedQuantity(value.getRestockedQuantity())
        .build();
  }
}
