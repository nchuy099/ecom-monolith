package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.InventoryResponse;
import com.ecomlab.ecommerce.dto.response.ProductSummaryResponse;
import com.ecomlab.ecommerce.dto.response.ProductVariantDetailResponse;
import com.ecomlab.ecommerce.service.ProductCatalogService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductCatalogService productCatalogService;

  @GetMapping("/{productId}")
  public ResponseEntity<List<ProductVariantDetailResponse>> detail(@PathVariable UUID productId) {
    return ResponseEntity.ok(productCatalogService.detail(productId));
  }

  @GetMapping("/{productId}/inventory")
  public ResponseEntity<List<InventoryResponse>> inventory(@PathVariable UUID productId) {
    return ResponseEntity.ok(productCatalogService.inventory(productId));
  }

  @GetMapping
  public ResponseEntity<CursorPageResponse<ProductSummaryResponse>> search(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) UUID categoryId,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @RequestParam(defaultValue = "false") boolean inStockOnly,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
    return ResponseEntity.ok(
        productCatalogService.search(
            keyword, categoryId, minPrice, maxPrice, inStockOnly, sort, cursor, size));
  }
}
