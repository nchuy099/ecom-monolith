package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.dto.response.ProductListCursor;
import com.ecomlab.ecommerce.dto.response.ProductListProjection;
import com.ecomlab.ecommerce.entity.InventoryEntity;
import com.ecomlab.ecommerce.entity.ProductEntity;
import com.ecomlab.ecommerce.entity.ProductVariantEntity;
import com.ecomlab.ecommerce.specification.ProductVariantSpecifications;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class ProductVariantQueryRepositoryImpl implements ProductVariantQueryRepository {
  private final EntityManager entityManager;

  public ProductVariantQueryRepositoryImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public List<ProductListProjection> findCatalogPageAfterCursor(
      String keyword,
      UUID categoryId,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      ProductListCursor cursor,
      int limit) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ProductListProjection> query =
        criteriaBuilder.createQuery(ProductListProjection.class);
    Root<ProductVariantEntity> variantRoot = query.from(ProductVariantEntity.class);
    Join<ProductVariantEntity, ProductEntity> productJoin = variantRoot.join("product");

    Expression<Long> availableQuantity =
        stockQuantity(query, criteriaBuilder, variantRoot, "availableQuantity");
    Expression<Long> reservedQuantity =
        stockQuantity(query, criteriaBuilder, variantRoot, "reservedQuantity");
    Specification<ProductVariantEntity> specification =
        Specification.allOf(
            ProductVariantSpecifications.visibleToCatalog(),
            ProductVariantSpecifications.keywordContains(keyword),
            ProductVariantSpecifications.hasCategory(categoryId),
            ProductVariantSpecifications.hasMinPrice(minPrice),
            ProductVariantSpecifications.hasMaxPrice(maxPrice));
    Predicate filterPredicate = specification.toPredicate(variantRoot, query, criteriaBuilder);
    Predicate cursorPredicate = afterCursor(variantRoot, criteriaBuilder, cursor);

    query.select(
        criteriaBuilder.construct(
            ProductListProjection.class,
            productJoin.get("id"),
            productJoin.get("name"),
            productJoin.get("category").get("id"),
            productJoin.get("active"),
            variantRoot.get("id"),
            variantRoot.get("sku"),
            variantRoot.get("name"),
            variantRoot.get("price"),
            availableQuantity,
            reservedQuantity));
    query.where(criteriaBuilder.and(filterPredicate, cursorPredicate));
    query.orderBy(
        criteriaBuilder.asc(variantRoot.get("price")), criteriaBuilder.asc(variantRoot.get("id")));

    return entityManager.createQuery(query).setMaxResults(limit).getResultList();
  }

  private Expression<Long> stockQuantity(
      CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder,
      Root<ProductVariantEntity> variantRoot,
      String quantityField) {
    Subquery<Long> subquery = query.subquery(Long.class);
    Root<InventoryEntity> inventoryRoot = subquery.from(InventoryEntity.class);

    subquery.select(
        criteriaBuilder.coalesce(criteriaBuilder.sumAsLong(inventoryRoot.get(quantityField)), 0L));
    subquery.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(inventoryRoot.get("variant"), variantRoot),
            criteriaBuilder.isFalse(inventoryRoot.get("isDeleted"))));

    return subquery;
  }

  private Predicate afterCursor(
      Root<ProductVariantEntity> variantRoot,
      CriteriaBuilder criteriaBuilder,
      ProductListCursor cursor) {
    if (cursor == null || cursor.price() == null || cursor.id() == null) {
      return criteriaBuilder.conjunction();
    }

    return criteriaBuilder.or(
        criteriaBuilder.greaterThan(variantRoot.get("price"), cursor.price()),
        criteriaBuilder.and(
            criteriaBuilder.equal(variantRoot.get("price"), cursor.price()),
            criteriaBuilder.greaterThan(variantRoot.get("id"), cursor.id())));
  }
}
