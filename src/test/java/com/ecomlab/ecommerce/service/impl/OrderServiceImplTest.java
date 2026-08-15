package com.ecomlab.ecommerce.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.OrderListCursor;
import com.ecomlab.ecommerce.dto.response.OrderResponse;
import com.ecomlab.ecommerce.entity.OrderEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.OrderRepository;
import com.ecomlab.ecommerce.repository.ShipmentRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
  @Mock private OrderRepository orderRepository;
  @Mock private ShipmentRepository shipmentRepository;

  @Test
  void get_my_orders_uses_page_size_plus_one_and_builds_next_cursor() {
    OrderServiceImpl service = new OrderServiceImpl(orderRepository, shipmentRepository);
    UUID userId = UUID.randomUUID();
    OrderEntity first = order(OrderStatus.CONFIRMED, "2026-09-16T10:00:00Z");
    OrderEntity second = order(OrderStatus.CONFIRMED, "2026-09-16T09:00:00Z");

    when(orderRepository.findMyOrdersAfterCursor(userId, OrderStatus.CONFIRMED, null, 2))
        .thenReturn(List.of(first, second));

    CursorPageResponse<OrderResponse> response =
        service.getMyOrders(userId, OrderStatus.CONFIRMED, null, 1);

    assertThat(response.isHasNext()).isTrue();
    assertThat(response.getContent()).hasSize(1);
    assertThat(response.getNextCursor()).isNotBlank();
    assertThat(OrderListCursor.decode(response.getNextCursor()).id()).isEqualTo(first.getId());
  }

  @Test
  void get_my_orders_rejects_cursor_from_different_status_filter() {
    OrderServiceImpl service = new OrderServiceImpl(orderRepository, shipmentRepository);
    UUID userId = UUID.randomUUID();
    OrderListCursor cursor =
        OrderListCursor.from(
            order(OrderStatus.CONFIRMED, "2026-09-16T10:00:00Z"), OrderStatus.CONFIRMED);

    assertThatThrownBy(
            () -> service.getMyOrders(userId, OrderStatus.CANCELLED, cursor.encode(), 10))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("Cursor does not match");

    verify(orderRepository, never())
        .findMyOrdersAfterCursor(eq(userId), eq(OrderStatus.CANCELLED), eq(cursor), eq(11));
  }

  private OrderEntity order(OrderStatus status, String createdAt) {
    OrderEntity order = new OrderEntity();
    order.setId(UUID.randomUUID());
    order.setOrderNumber("ORD-" + order.getId());
    order.setStatus(status);
    order.setTotalAmount(new BigDecimal("199.99"));
    order.setCity("HCM");
    order.setItems(new ArrayList<>());
    order.setCreatedAt(Instant.parse(createdAt));
    return order;
  }
}
