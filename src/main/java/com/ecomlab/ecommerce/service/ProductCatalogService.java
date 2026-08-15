package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.ProductSummaryResponse;
import com.ecomlab.ecommerce.dto.response.ProductVariantDetailResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductCatalogService {
  List<ProductVariantDetailResponse> detail(UUID productId);

  CursorPageResponse<ProductSummaryResponse> search(
      String keyword,
      UUID categoryId,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      String cursor,
      int size);

  void evictDetail(UUID productId);
}
