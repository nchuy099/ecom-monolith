package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.OrderResponse;
import java.util.UUID;

public interface OrderService {
  CursorPageResponse<OrderResponse> getMyOrders(
      UUID userId, OrderStatus status, String cursor, int size);

  OrderResponse getDetails(UUID userId, UUID orderId);

  OrderResponse cancel(UUID userId, UUID orderId);
}
