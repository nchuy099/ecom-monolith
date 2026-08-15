package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.response.NotificationResponse;
import com.ecomlab.ecommerce.dto.response.PageResponse;
import com.ecomlab.ecommerce.service.NotificationService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminNotificationController {
  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<PageResponse<NotificationResponse>> list(
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
    return ResponseEntity.ok(notificationService.adminPage(status, page, size));
  }
}
