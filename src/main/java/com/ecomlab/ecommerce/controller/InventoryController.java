package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.response.InventoryResponse;
import com.ecomlab.ecommerce.service.InventoryLookupService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
  private final InventoryLookupService inventoryLookupService;

  @GetMapping("/variants/{variantId}/warehouses")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<List<InventoryResponse>> warehousesByVariant(
      Authentication authentication, @PathVariable UUID variantId, @RequestParam UUID addressId) {
    return ResponseEntity.ok(
        inventoryLookupService.warehousesByVariant(
            UUID.fromString(authentication.getName()), variantId, addressId));
  }
}
