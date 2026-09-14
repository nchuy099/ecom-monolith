package com.ecomlab.ecommerce.specification;

import com.ecomlab.ecommerce.entity.InventoryEntity;
import com.ecomlab.ecommerce.entity.ProductVariantEntity;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class ProductVariantSpecifications {
  private ProductVariantSpecifications() {}

  public static Specification<ProductVariantEntity> visibleToCatalog() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.isFalse(root.get("isDeleted")),
            criteriaBuilder.isTrue(root.get("active")),
            criteriaBuilder.isFalse(root.get("product").get("isDeleted")),
            criteriaBuilder.isTrue(root.get("product").get("active")));
  }

  public static Specification<ProductVariantEntity> keywordContains(String keyword) {
    return (root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.isBlank()) {
        return criteriaBuilder.conjunction();
      }

      String pattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";

      return criteriaBuilder.or(
          criteriaBuilder.like(criteriaBuilder.lower(root.get("product").get("name")), pattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("sku")), pattern));
    };
  }

  public static Specification<ProductVariantEntity> hasCategory(UUID categoryId) {
    return (root, query, criteriaBuilder) ->
        categoryId == null
            ? criteriaBuilder.conjunction()
            : criteriaBuilder.equal(root.get("product").get("category").get("id"), categoryId);
  }

  public static Specification<ProductVariantEntity> hasMinPrice(BigDecimal minPrice) {
    return (root, query, criteriaBuilder) ->
        minPrice == null
            ? criteriaBuilder.conjunction()
            : criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
  }

  public static Specification<ProductVariantEntity> hasMaxPrice(BigDecimal maxPrice) {
    return (root, query, criteriaBuilder) ->
        maxPrice == null
            ? criteriaBuilder.conjunction()
            : criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
  }

  public static Specification<ProductVariantEntity> inStockOnly(boolean inStockOnly) {
    return (root, query, criteriaBuilder) -> {
      if (!inStockOnly) {
        return criteriaBuilder.conjunction();
      }

      var subquery = query.subquery(Long.class);
      var inventoryRoot = subquery.from(InventoryEntity.class);
      subquery.select(criteriaBuilder.literal(1L));
      subquery.where(
          criteriaBuilder.and(
              criteriaBuilder.equal(inventoryRoot.get("variant"), root),
              criteriaBuilder.isFalse(inventoryRoot.get("isDeleted")),
              criteriaBuilder.greaterThan(inventoryRoot.get("availableQuantity"), 0)));

      return criteriaBuilder.exists(subquery);
    };
  }
}
