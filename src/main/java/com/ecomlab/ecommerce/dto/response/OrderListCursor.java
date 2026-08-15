package com.ecomlab.ecommerce.dto.response;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.entity.OrderEntity;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

public record OrderListCursor(OrderStatus status, Instant createdAt, UUID id) {
  private static final String NULL_VALUE = "-";
  private static final String SEPARATOR = "\\|";

  public static OrderListCursor from(OrderEntity order, OrderStatus status) {
    return new OrderListCursor(status, order.getCreatedAt(), order.getId());
  }

  public String encode() {
    String raw = String.join("|", value(status), value(createdAt), value(id));
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
  }

  public static OrderListCursor decode(String encoded) {
    String raw = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
    String[] fields = raw.split(SEPARATOR, -1);
    if (fields.length != 3) {
      throw new IllegalArgumentException("Invalid cursor");
    }

    return new OrderListCursor(
        nullableStatus(fields[0]), nullableInstant(fields[1]), nullableUuid(fields[2]));
  }

  public boolean matches(OrderStatus status) {
    return this.status == status;
  }

  private static String value(Object value) {
    return value == null ? NULL_VALUE : value.toString();
  }

  private static OrderStatus nullableStatus(String value) {
    return NULL_VALUE.equals(value) ? null : OrderStatus.valueOf(value);
  }

  private static Instant nullableInstant(String value) {
    return NULL_VALUE.equals(value) ? null : Instant.parse(value);
  }

  private static UUID nullableUuid(String value) {
    return NULL_VALUE.equals(value) ? null : UUID.fromString(value);
  }
}
