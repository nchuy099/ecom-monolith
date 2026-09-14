package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.TrackingRequest;
import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import com.ecomlab.ecommerce.service.ShipperShipmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipper/shipments")
@PreAuthorize("hasRole('SHIPPER')")
@RequiredArgsConstructor
public class ShipperShipmentController {
  private final ShipperShipmentService shipperShipmentService;

  @GetMapping
  public ResponseEntity<List<ShipmentResponse>> getAssignedShipments(Authentication auth) {
    return ResponseEntity.ok(
        shipperShipmentService.getAssignedShipments(UUID.fromString(auth.getName())));
  }

  @GetMapping("/today")
  public ResponseEntity<List<ShipmentResponse>> getTodayShipments(Authentication auth) {
    return ResponseEntity.ok(
        shipperShipmentService.getTodayShipments(UUID.fromString(auth.getName())));
  }

  @GetMapping("/lookup")
  public ResponseEntity<ShipmentResponse> lookup(
      Authentication auth, @RequestParam @NotBlank String trackingNumber) {
    return ResponseEntity.ok(
        shipperShipmentService.findByTrackingNumber(
            UUID.fromString(auth.getName()), trackingNumber));
  }

  @PostMapping("/{shipmentId}/tracking")
  public ResponseEntity<Void> tracking(
      Authentication auth,
      @PathVariable UUID shipmentId,
      @Valid @RequestBody TrackingRequest request) {
    shipperShipmentService.updateTracking(
        shipmentId,
        UUID.fromString(auth.getName()),
        request.getStatus(),
        request.getNote(),
        request.getLatitude(),
        request.getLongitude(),
        request.getProofUrl());
    return ResponseEntity.noContent().build();
  }
}
