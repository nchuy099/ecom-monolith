package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.WarehouseRequest;
import com.ecomlab.ecommerce.dto.request.WarehouseShippingZoneRequest;
import com.ecomlab.ecommerce.dto.response.WarehouseResponse;
import com.ecomlab.ecommerce.dto.response.WarehouseShippingZoneResponse;
import com.ecomlab.ecommerce.service.WarehouseService;
import com.ecomlab.ecommerce.service.WarehouseShippingZoneService;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/warehouses")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminWarehouseController {
  private final WarehouseService warehouseService;
  private final WarehouseShippingZoneService warehouseShippingZoneService;

  @GetMapping
  public ResponseEntity<List<WarehouseResponse>> list() {
    return ResponseEntity.ok(warehouseService.warehouses());
  }

  @PostMapping
  public ResponseEntity<WarehouseResponse> create(@Valid @RequestBody WarehouseRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.create(r));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<WarehouseResponse> update(
      @PathVariable UUID id, @Valid @RequestBody WarehouseRequest r) {
    return ResponseEntity.ok(warehouseService.update(id, r));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    warehouseService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/shipping-zones")
  public ResponseEntity<List<WarehouseShippingZoneResponse>> shippingZones(@PathVariable UUID id) {
    return ResponseEntity.ok(warehouseShippingZoneService.zones(id));
  }

  @PutMapping("/{id}/shipping-zones")
  public ResponseEntity<List<WarehouseShippingZoneResponse>> replaceShippingZones(
      @PathVariable UUID id, @Valid @RequestBody WarehouseShippingZoneRequest request) {
    return ResponseEntity.ok(warehouseShippingZoneService.replace(id, request));
  }
}
