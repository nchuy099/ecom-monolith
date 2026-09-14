package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.ShippingZoneRequest;
import com.ecomlab.ecommerce.dto.response.ShippingZoneResponse;
import com.ecomlab.ecommerce.service.ShippingZoneService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/shipping-zones")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminShippingZoneController {
  private final ShippingZoneService shippingZoneService;

  @GetMapping
  public ResponseEntity<List<ShippingZoneResponse>> list() {
    return ResponseEntity.ok(shippingZoneService.shippingZones());
  }

  @PostMapping
  public ResponseEntity<ShippingZoneResponse> create(
      @Valid @RequestBody ShippingZoneRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(shippingZoneService.create(request));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ShippingZoneResponse> update(
      @PathVariable UUID id, @Valid @RequestBody ShippingZoneRequest request) {
    return ResponseEntity.ok(shippingZoneService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    shippingZoneService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
