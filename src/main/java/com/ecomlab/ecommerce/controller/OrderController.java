package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.OrderResponse;
import com.ecomlab.ecommerce.service.OrderService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;

  @GetMapping
  public ResponseEntity<CursorPageResponse<OrderResponse>> getMyOrders(
      Authentication a,
      @RequestParam(required = false) OrderStatus status,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
    return ResponseEntity.ok(
        orderService.getMyOrders(UUID.fromString(a.getName()), status, cursor, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getDetails(Authentication a, @PathVariable UUID id) {
    return ResponseEntity.ok(orderService.getDetails(UUID.fromString(a.getName()), id));
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<OrderResponse> cancel(Authentication a, @PathVariable UUID id) {
    return ResponseEntity.ok(orderService.cancel(UUID.fromString(a.getName()), id));
  }
}
