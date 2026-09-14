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
      boolean inStockOnly,
      String sort,
      ProductListCursor cursor,
      int limit);

  default List<ProductListProjection> findCatalogPageAfterCursor(
      String keyword,
      UUID categoryId,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      ProductListCursor cursor,
      int limit) {
    return findCatalogPageAfterCursor(
        keyword, categoryId, minPrice, maxPrice, false, null, cursor, limit);
  }
}
