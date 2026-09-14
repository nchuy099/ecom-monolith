package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.OrderResponse;
import java.util.Map;
import java.util.UUID;

public interface OrderService {
  CursorPageResponse<OrderResponse> getMyOrders(
      UUID userId, OrderStatus status, String cursor, int size);

  CursorPageResponse<OrderResponse> getOrders(OrderStatus status, String cursor, int size);

  Map<String, Object> summary();

  OrderResponse getDetails(UUID userId, UUID orderId);

  OrderResponse cancel(UUID userId, UUID orderId);
}
