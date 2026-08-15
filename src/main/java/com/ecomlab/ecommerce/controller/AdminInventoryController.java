package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.AdjustInventoryRequest;
import com.ecomlab.ecommerce.dto.request.UpsertInventoryRequest;
import com.ecomlab.ecommerce.dto.response.InventoryResponse;
import com.ecomlab.ecommerce.service.InventoryAdminService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminInventoryController {
  private final InventoryAdminService inventoryAdminService;

  @GetMapping
  public ResponseEntity<List<InventoryResponse>> list(
      @RequestParam(required = false) UUID warehouseId,
      @RequestParam(required = false) UUID variantId) {
    return ResponseEntity.ok(inventoryAdminService.list(warehouseId, variantId));
  }

  @PutMapping
  public ResponseEntity<InventoryResponse> upsert(
      @Valid @RequestBody UpsertInventoryRequest request) {
    return ResponseEntity.ok(inventoryAdminService.upsert(request));
  }

  @PostMapping("/adjust")
  public ResponseEntity<InventoryResponse> adjust(
      @Valid @RequestBody AdjustInventoryRequest request) {
    return ResponseEntity.ok(inventoryAdminService.adjust(request));
  }
}
