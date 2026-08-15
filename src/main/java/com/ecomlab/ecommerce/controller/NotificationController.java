package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.response.NotificationResponse;
import com.ecomlab.ecommerce.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class NotificationController {
  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationResponse>> getMyNotifications(
      Authentication authentication) {
    return ResponseEntity.ok(
        notificationService.getMyNotifications(UUID.fromString(authentication.getName())));
  }

  @PostMapping("/{id}/read")
  public ResponseEntity<NotificationResponse> read(
      Authentication authentication, @PathVariable UUID id) {
    return ResponseEntity.ok(
        notificationService.read(UUID.fromString(authentication.getName()), id));
  }
}
