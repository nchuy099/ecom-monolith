package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.dto.response.ProductListCursor;
import com.ecomlab.ecommerce.dto.response.ProductListProjection;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductVariantQueryRepository {
  List<ProductListProjection> findCatalogPageAfterCursor(
      String keyword,
      UUID categoryId,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      ProductListCursor cursor,
      int limit);
}
