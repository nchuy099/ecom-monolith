package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.OrderResponse;
import com.ecomlab.ecommerce.service.OrderService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminOrderController {
  private final OrderService orderService;

  @GetMapping
  public ResponseEntity<CursorPageResponse<OrderResponse>> list(
      @RequestParam(required = false) OrderStatus status,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
    return ResponseEntity.ok(orderService.getOrders(status, cursor, size));
  }

  @GetMapping("/summary")
  public ResponseEntity<Map<String, Object>> summary() {
    return ResponseEntity.ok(orderService.summary());
  }
}
