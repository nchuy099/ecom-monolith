package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.ProductListCursor;
import com.ecomlab.ecommerce.dto.response.ProductListProjection;
import com.ecomlab.ecommerce.dto.response.ProductSummaryResponse;
import com.ecomlab.ecommerce.dto.response.ProductVariantDetailResponse;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.ProductVariantRepository;
import com.ecomlab.ecommerce.service.ProductCatalogService;
import com.ecomlab.ecommerce.service.builder.ProductResponseBuilder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCatalogServiceImpl implements ProductCatalogService {
  private final ProductVariantRepository productVariantRepository;

  /**
   * JPQL projection deliberately avoids loading ProductEntity/Variant entities and their
   * associations for detail reads.
   */
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "productDetail", key = "#productId")
  public List<ProductVariantDetailResponse> detail(UUID productId) {
    return productVariantRepository.findVisibleDetailByProductId(productId).stream()
        .map(ProductResponseBuilder::detail)
        .toList();
  }

  @Transactional(readOnly = true)
  public CursorPageResponse<ProductSummaryResponse> search(
      String keyword,
      UUID categoryId,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      String cursor,
      int size) {
    validatePriceRange(minPrice, maxPrice);

    int pageSize = Math.max(1, Math.min(size, 100));
    String normalizedKeyword = normalizeKeyword(keyword);
    ProductListCursor position =
        parseCursor(cursor, normalizedKeyword, categoryId, minPrice, maxPrice);
    List<ProductListProjection> rows =
        productVariantRepository.findCatalogPageAfterCursor(
            normalizedKeyword, categoryId, minPrice, maxPrice, position, pageSize + 1);

    boolean hasNext = rows.size() > pageSize;
    List<ProductListProjection> content = hasNext ? rows.subList(0, pageSize) : rows;
    String nextCursor =
        hasNext
            ? ProductListCursor.from(
                    content.get(content.size() - 1),
                    normalizedKeyword,
                    categoryId,
                    minPrice,
                    maxPrice)
                .encode()
            : null;

    return CursorPageResponse.<ProductSummaryResponse>builder()
        .content(content.stream().map(ProductResponseBuilder::summary).toList())
        .size(pageSize)
        .hasNext(hasNext)
        .nextCursor(nextCursor)
        .build();
  }

  @CacheEvict(cacheNames = "productDetail", key = "#productId")
  public void evictDetail(UUID productId) {}

  private ProductListCursor parseCursor(
      String cursor, String keyword, UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
    if (cursor == null || cursor.isBlank()) {
      return null;
    }

    try {
      ProductListCursor parsed = ProductListCursor.decode(cursor);
      if (!parsed.matches(keyword, categoryId, minPrice, maxPrice)) {
        throw new IllegalArgumentException("Cursor does not match the current product filter");
      }

      return parsed;
    } catch (IllegalArgumentException exception) {
      throw new BusinessException("INVALID_CURSOR", exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  private void validatePriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
    if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
      throw new BusinessException(
          "INVALID_PRICE_RANGE",
          "minPrice must be less than or equal to maxPrice",
          HttpStatus.BAD_REQUEST);
    }
  }

  private String normalizeKeyword(String keyword) {
    return keyword == null || keyword.isBlank() ? null : keyword.trim().toLowerCase(Locale.ROOT);
  }
}
