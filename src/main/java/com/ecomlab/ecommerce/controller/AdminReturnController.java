package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.*;
import com.ecomlab.ecommerce.dto.response.ReturnResponse;
import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import com.ecomlab.ecommerce.service.ReturnService;
import com.ecomlab.ecommerce.service.ShipmentAdminService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/returns")
@RequiredArgsConstructor
public class AdminReturnController {
  private final ReturnService returnService;
  private final ShipmentAdminService shipmentAdminService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF')")
  public ResponseEntity<List<ReturnResponse>> list() {
    return ResponseEntity.ok(returnService.adminReturns());
  }

  @GetMapping("/{returnId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF')")
  public ResponseEntity<ReturnResponse> get(@PathVariable UUID returnId) {
    return ResponseEntity.ok(returnService.adminReturn(returnId));
  }

  @PostMapping("/{returnId}/approve")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ReturnResponse> approve(
      @PathVariable UUID returnId, @Valid @RequestBody ReturnDecisionRequest request) {
    return ResponseEntity.ok(returnService.approve(returnId, request));
  }

  @PostMapping("/{returnId}/reject")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ReturnResponse> reject(
      @PathVariable UUID returnId, @Valid @RequestBody ReturnDecisionRequest request) {
    return ResponseEntity.ok(returnService.reject(returnId, request));
  }

  @PostMapping("/shipments/{shipmentId}/assign")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ShipmentResponse> assign(
      @PathVariable UUID shipmentId, @Valid @RequestBody AssignShipperRequest request) {
    return ResponseEntity.ok(shipmentAdminService.assign(shipmentId, request.getShipperId()));
  }

  @PostMapping("/shipments/{shipmentId}/receive")
  @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF')")
  public ResponseEntity<ReturnResponse> receive(
      @PathVariable UUID shipmentId, @Valid @RequestBody ReceiveReturnShipmentRequest request) {
    return ResponseEntity.ok(returnService.receive(shipmentId, request));
  }

  @PostMapping("/{returnId}/refund")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ReturnResponse> refund(@PathVariable UUID returnId) {
    return ResponseEntity.ok(returnService.refund(returnId));
  }
}
