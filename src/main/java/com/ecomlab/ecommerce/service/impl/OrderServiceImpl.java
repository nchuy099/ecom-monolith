package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.common.enums.ShipmentStatus;
import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.OrderListCursor;
import com.ecomlab.ecommerce.dto.response.OrderResponse;
import com.ecomlab.ecommerce.entity.OrderEntity;
import com.ecomlab.ecommerce.entity.ShipmentEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.OrderRepository;
import com.ecomlab.ecommerce.repository.ShipmentRepository;
import com.ecomlab.ecommerce.service.OrderService;
import com.ecomlab.ecommerce.service.builder.OrderResponseBuilder;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
  private final OrderRepository orderRepository;
  private final ShipmentRepository shipmentRepository;

  @Transactional(readOnly = true)
  public CursorPageResponse<OrderResponse> getMyOrders(
      UUID userId, OrderStatus status, String cursor, int size) {
    int pageSize = Math.max(1, Math.min(size, 100));
    OrderListCursor position = parseCursor(cursor, status);
    List<OrderEntity> rows =
        orderRepository.findMyOrdersAfterCursor(userId, status, position, pageSize + 1);
    boolean hasNext = rows.size() > pageSize;
    List<OrderEntity> content = hasNext ? rows.subList(0, pageSize) : rows;
    String nextCursor =
        hasNext ? OrderListCursor.from(content.get(content.size() - 1), status).encode() : null;

    return CursorPageResponse.<OrderResponse>builder()
        .content(content.stream().map(OrderResponseBuilder::build).toList())
        .size(pageSize)
        .hasNext(hasNext)
        .nextCursor(nextCursor)
        .build();
  }

  @Transactional(readOnly = true)
  public OrderResponse getDetails(UUID userId, UUID orderId) {
    return OrderResponseBuilder.build(owned(userId, orderId));
  }

  @Transactional
  public OrderResponse cancel(UUID userId, UUID orderId) {
    OrderEntity order = owned(userId, orderId);
    if (order.getStatus() != OrderStatus.PENDING_PAYMENT
        && order.getStatus() != OrderStatus.CONFIRMED)
      throw new BusinessException(
          "ORDER_CANNOT_BE_CANCELLED", "Order cannot be cancelled", HttpStatus.CONFLICT);

    shipmentRepository.findActiveByOrderIdWithItems(order.getId()).stream()
        .filter(shipment -> shipment.getStatus() != ShipmentStatus.DELIVERED)
        .forEach(this::cancelShipmentAndReleaseStock);
    order.setStatus(OrderStatus.CANCELLED);
    return OrderResponseBuilder.build(order);
  }

  private void cancelShipmentAndReleaseStock(ShipmentEntity shipment) {
    shipment.setStatus(ShipmentStatus.CANCELLED);
    shipment.getItems().forEach(item -> item.getInventory().release(item.getQuantity()));
  }

  private OrderListCursor parseCursor(String cursor, OrderStatus status) {
    if (cursor == null || cursor.isBlank()) {
      return null;
    }

    try {
      OrderListCursor parsed = OrderListCursor.decode(cursor);
      if (!parsed.matches(status)) {
        throw new IllegalArgumentException("Cursor does not match the current order filter");
      }

      return parsed;
    } catch (IllegalArgumentException exception) {
      throw new BusinessException("INVALID_CURSOR", exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  private OrderEntity owned(UUID userId, UUID orderId) {
    return orderRepository
        .findOwnedByIdWithItems(orderId, userId)
        .orElseThrow(
            () ->
                new BusinessException("ORDER_NOT_FOUND", "Order not found", HttpStatus.NOT_FOUND));
  }
}
