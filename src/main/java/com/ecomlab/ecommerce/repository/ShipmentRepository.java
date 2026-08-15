package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<ShipmentEntity, UUID> {
  @Query(
      """
      select s from ShipmentEntity s join fetch s.order o join fetch s.warehouse left join fetch s.shipper
      where o.user.id = :userId and s.isDeleted = false order by s.createdAt desc
      """)
  List<ShipmentEntity> findAllByCustomerId(@Param("userId") UUID userId);

  @Query(
      """
      select s
      from ShipmentEntity s
      join fetch s.warehouse
      where s.shipper.id = :shipperId
        and s.isDeleted = false
      order by s.updatedAt desc
      """)
  List<ShipmentEntity> findAssignedByShipperId(@Param("shipperId") UUID shipperId);

  @Query(
      """
      select distinct s
      from ShipmentEntity s
      join fetch s.order o
      left join fetch s.items si
      left join fetch si.inventory
      where o.id = :orderId
        and s.isDeleted = false
      """)
  List<ShipmentEntity> findActiveByOrderIdWithItems(@Param("orderId") UUID orderId);

  @Query(
      """
      select distinct s
      from ShipmentEntity s
      join fetch s.order o
      left join fetch s.items si
      where o.id = :orderId
        and s.isDeleted = false
      """)
  List<ShipmentEntity> findActiveByOrderId(@Param("orderId") UUID orderId);
}
