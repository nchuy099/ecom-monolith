package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.WarehouseShippingZoneEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseShippingZoneRepository
    extends JpaRepository<WarehouseShippingZoneEntity, UUID> {
  @Query(
      """
      select wz
      from WarehouseShippingZoneEntity wz
      join fetch wz.warehouse w
      join fetch wz.shippingZone z
      where w.id = :warehouseId
        and wz.isDeleted = false
        and z.isDeleted = false
      order by wz.priority asc, z.name asc
      """)
  List<WarehouseShippingZoneEntity> findByWarehouseId(@Param("warehouseId") UUID warehouseId);

  @Query(
      """
      select wz
      from WarehouseShippingZoneEntity wz
      join fetch wz.warehouse w
      join fetch wz.shippingZone z
      where z.id = :shippingZoneId
        and wz.active = true
        and wz.isDeleted = false
        and w.active = true
        and w.isDeleted = false
        and z.active = true
        and z.isDeleted = false
      order by wz.priority asc, w.id asc
      """)
  List<WarehouseShippingZoneEntity> findActiveByShippingZoneId(
      @Param("shippingZoneId") UUID shippingZoneId);

  Optional<WarehouseShippingZoneEntity> findByWarehouseIdAndShippingZoneIdAndIsDeletedFalse(
      UUID warehouseId, UUID shippingZoneId);

  Optional<WarehouseShippingZoneEntity> findByWarehouseIdAndShippingZoneId(
      UUID warehouseId, UUID shippingZoneId);
}
