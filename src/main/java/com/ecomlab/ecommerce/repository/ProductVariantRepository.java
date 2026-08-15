package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.dto.response.ProductDetailProjection;
import com.ecomlab.ecommerce.entity.ProductVariantEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository
    extends JpaRepository<ProductVariantEntity, UUID>, ProductVariantQueryRepository {
  @Query(
      """
      select v from ProductVariantEntity v join fetch v.product p
      where p.id = :productId and v.id = :variantId and p.isDeleted = false and v.isDeleted = false
      """)
  Optional<ProductVariantEntity> findActiveByProductIdAndId(
      @Param("productId") UUID productId, @Param("variantId") UUID variantId);

  @Query(
      """
      select v from ProductVariantEntity v join fetch v.product p
      where p.id = :productId and p.active = true and p.isDeleted = false and v.active = true and v.isDeleted = false
      """)
  List<ProductVariantEntity> findVisibleByProductId(@Param("productId") UUID productId);

  Optional<ProductVariantEntity> findByIdAndIsDeletedFalseAndActiveTrue(UUID id);

  @Query(
      """
      select new com.ecomlab.ecommerce.dto.response.ProductDetailProjection(
        p.id,
        p.name,
        v.id,
        v.sku,
        v.name,
        v.price,
        coalesce(sum(i.availableQuantity), 0),
        coalesce(sum(i.reservedQuantity), 0)
      )
      from ProductVariantEntity v
      join v.product p
      left join InventoryEntity i on i.variant = v and i.isDeleted = false
      where p.id = :productId
        and p.active = true
        and p.isDeleted = false
        and v.active = true
        and v.isDeleted = false
      group by p.id, p.name, v.id, v.sku, v.name, v.price
      order by v.price asc, v.id asc
      """)
  List<ProductDetailProjection> findVisibleDetailByProductId(@Param("productId") UUID productId);
}
