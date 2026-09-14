package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.CreateProductRequest;
import com.ecomlab.ecommerce.dto.request.CreateProductVariantRequest;
import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.ProductAdminResponse;
import com.ecomlab.ecommerce.dto.response.ProductSummaryResponse;
import com.ecomlab.ecommerce.dto.response.ProductVariantAdminResponse;
import com.ecomlab.ecommerce.service.AdminProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminProductController {
  private final AdminProductService adminProductService;

  @GetMapping
  public ResponseEntity<CursorPageResponse<ProductSummaryResponse>> list(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) UUID categoryId,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @RequestParam(defaultValue = "false") boolean inStockOnly,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
    return ResponseEntity.ok(
        adminProductService.list(
            keyword, categoryId, minPrice, maxPrice, inStockOnly, sort, cursor, size));
  }

  @PostMapping
  public ResponseEntity<ProductAdminResponse> create(
      @Valid @RequestBody CreateProductRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(adminProductService.create(request));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ProductAdminResponse> update(
      @PathVariable UUID id, @Valid @RequestBody CreateProductRequest request) {
    return ResponseEntity.ok(adminProductService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    adminProductService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{productId}/variants")
  public ResponseEntity<ProductVariantAdminResponse> createVariant(
      @PathVariable UUID productId, @Valid @RequestBody CreateProductVariantRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(adminProductService.createVariant(productId, request));
  }

  @PatchMapping("/{productId}/variants/{variantId}")
  public ResponseEntity<ProductVariantAdminResponse> updateVariant(
      @PathVariable UUID productId,
      @PathVariable UUID variantId,
      @Valid @RequestBody CreateProductVariantRequest request) {
    return ResponseEntity.ok(adminProductService.updateVariant(productId, variantId, request));
  }

  @DeleteMapping("/{productId}/variants/{variantId}")
  public ResponseEntity<Void> deleteVariant(
      @PathVariable UUID productId, @PathVariable UUID variantId) {
    adminProductService.deleteVariant(productId, variantId);
    return ResponseEntity.noContent().build();
  }
}
