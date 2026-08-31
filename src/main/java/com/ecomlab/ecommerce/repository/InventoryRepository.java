package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.InventoryEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, UUID> {
  @Query(
      """
      select distinct i
      from InventoryEntity i
      join fetch i.warehouse
      join fetch i.variant v
      join fetch v.product
      where i.isDeleted = false
        and (:warehouseId is null or i.warehouse.id = :warehouseId)
        and (:variantId is null or i.variant.id = :variantId)
      order by i.updatedAt desc
      """)
  List<InventoryEntity> findActiveByFilters(
      @Param("warehouseId") UUID warehouseId, @Param("variantId") UUID variantId);

  @Query(
      """
      select i
      from InventoryEntity i
      join fetch i.warehouse
      join fetch i.variant v
      join fetch v.product
      where i.warehouse.id = :warehouseId
        and i.variant.id = :variantId
        and i.isDeleted = false
      """)
  Optional<InventoryEntity> findActiveByWarehouseIdAndVariantId(
      @Param("warehouseId") UUID warehouseId, @Param("variantId") UUID variantId);

  @Query(
      """
      select i
      from InventoryEntity i
      join fetch i.variant
      where i.warehouse.id = :warehouseId
        and i.variant.id in :variantIds
        and i.isDeleted = false
      order by i.variant.id
      """)
  List<InventoryEntity> findByWarehouseAndVariants(
      @Param("warehouseId") UUID warehouseId, @Param("variantIds") Collection<UUID> variantIds);
}
