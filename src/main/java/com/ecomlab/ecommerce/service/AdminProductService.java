package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.request.CreateProductRequest;
import com.ecomlab.ecommerce.dto.request.CreateProductVariantRequest;
import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.ProductAdminResponse;
import com.ecomlab.ecommerce.dto.response.ProductSummaryResponse;
import com.ecomlab.ecommerce.dto.response.ProductVariantAdminResponse;
import java.math.BigDecimal;
import java.util.UUID;

public interface AdminProductService {
  CursorPageResponse<ProductSummaryResponse> list(
      String keyword,
      UUID categoryId,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      boolean inStockOnly,
      String sort,
      String cursor,
      int size);

  ProductAdminResponse create(CreateProductRequest request);

  ProductAdminResponse update(UUID id, CreateProductRequest request);

  void delete(UUID id);

  ProductVariantAdminResponse createVariant(UUID productId, CreateProductVariantRequest request);

  ProductVariantAdminResponse updateVariant(
      UUID productId, UUID variantId, CreateProductVariantRequest request);

  void deleteVariant(UUID productId, UUID variantId);
}
