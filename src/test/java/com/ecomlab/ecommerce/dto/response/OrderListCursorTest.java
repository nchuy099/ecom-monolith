package com.ecomlab.ecommerce.dto.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.entity.OrderEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderListCursorTest {
  @Test
  void encoded_cursor_round_trips_and_matches_same_filter() {
    OrderEntity order = new OrderEntity();
    UUID orderId = UUID.randomUUID();
    Instant createdAt = Instant.parse("2026-09-16T10:15:30Z");

    order.setId(orderId);
    order.setCreatedAt(createdAt);

    OrderListCursor cursor = OrderListCursor.from(order, OrderStatus.CONFIRMED);
    OrderListCursor decoded = OrderListCursor.decode(cursor.encode());

    assertThat(decoded.id()).isEqualTo(orderId);
    assertThat(decoded.createdAt()).isEqualTo(createdAt);
    assertThat(decoded.matches(OrderStatus.CONFIRMED)).isTrue();
    assertThat(decoded.matches(OrderStatus.CANCELLED)).isFalse();
  }

  @Test
  void decode_rejects_invalid_cursor() {
    assertThatThrownBy(() -> OrderListCursor.decode("not-a-valid-cursor"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
