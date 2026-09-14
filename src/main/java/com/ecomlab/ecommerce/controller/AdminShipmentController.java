package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.AssignShipperRequest;
import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import com.ecomlab.ecommerce.dto.response.UserResponse;
import com.ecomlab.ecommerce.service.ShipmentAdminService;
import com.ecomlab.ecommerce.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/shipments")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminShipmentController {
  private final ShipmentAdminService shipmentAdminService;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<List<ShipmentResponse>> list() {
    return ResponseEntity.ok(shipmentAdminService.list());
  }

  @GetMapping("/shippers")
  public ResponseEntity<List<UserResponse>> shippers() {
    return ResponseEntity.ok(userService.shippers());
  }

  @PostMapping("/{shipmentId}/assign")
  public ResponseEntity<ShipmentResponse> assign(
      @PathVariable UUID shipmentId, @Valid @RequestBody AssignShipperRequest request) {
    return ResponseEntity.ok(shipmentAdminService.assign(shipmentId, request.getShipperId()));
  }

  @PostMapping("/{shipmentId}/ready-for-pickup")
  public ResponseEntity<ShipmentResponse> readyForPickup(@PathVariable UUID shipmentId) {
    return ResponseEntity.ok(shipmentAdminService.readyForPickup(shipmentId));
  }
}
