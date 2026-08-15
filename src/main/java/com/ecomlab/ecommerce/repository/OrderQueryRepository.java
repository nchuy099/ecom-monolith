package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.dto.response.OrderListCursor;
import com.ecomlab.ecommerce.entity.OrderEntity;
import java.util.List;
import java.util.UUID;

public interface OrderQueryRepository {
  List<OrderEntity> findMyOrdersAfterCursor(
      UUID userId, OrderStatus status, OrderListCursor cursor, int limit);
}
