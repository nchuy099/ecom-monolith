package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.common.enums.ShipmentStatus;
import com.ecomlab.ecommerce.entity.*;
import java.time.Instant;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<ShipmentEntity, UUID> {
  @Query(
      """
      select distinct s
      from ShipmentEntity s
      join fetch s.order
      join fetch s.warehouse
      left join fetch s.shipper
      left join fetch s.items si
      left join fetch si.inventory
      where s.id = :shipmentId
        and s.isDeleted = false
      """)
  Optional<ShipmentEntity> findByIdWithItems(@Param("shipmentId") UUID shipmentId);

  @Query(
      """
      select s from ShipmentEntity s join fetch s.order o join fetch s.warehouse left join fetch s.shipper
      where o.user.id = :userId and s.isDeleted = false order by s.createdAt desc
      """)
  List<ShipmentEntity> findAllByCustomerId(@Param("userId") UUID userId);

  @Query(
      """
      select distinct s
      from ShipmentEntity s
      join fetch s.order o
      join fetch s.warehouse
      left join fetch s.shipper
      where o.id = :orderId
        and o.user.id = :userId
        and s.isDeleted = false
      order by s.createdAt asc
      """)
  List<ShipmentEntity> findOwnedByOrderId(
      @Param("userId") UUID userId, @Param("orderId") UUID orderId);

  @Query(
      """
      select distinct s
      from ShipmentEntity s
      join fetch s.order
      join fetch s.warehouse
      left join fetch s.shipper
      where s.isDeleted = false
      order by s.updatedAt desc
      """)
  List<ShipmentEntity> findAllActiveForAdmin();

  @Query(
      """
      select distinct s
      from ShipmentEntity s
      join fetch s.order
      join fetch s.warehouse
      left join fetch s.shipper
      where s.shipper.id = :shipperId
        and s.isDeleted = false
      order by s.updatedAt desc
      """)
  List<ShipmentEntity> findAssignedByShipperId(@Param("shipperId") UUID shipperId);

  @Query(
      """
      select distinct s
      from ShipmentEntity s
      join fetch s.order
      join fetch s.warehouse
      left join fetch s.shipper
      where s.shipper.id = :shipperId
        and s.isDeleted = false
        and (
          s.status in :activeStatuses
          or (s.status = :deliveredStatus and s.updatedAt >= :startOfDay)
        )
      order by s.status asc, s.updatedAt desc
      """)
  List<ShipmentEntity> findTodayByShipperId(
      @Param("shipperId") UUID shipperId,
      @Param("activeStatuses") Collection<ShipmentStatus> activeStatuses,
      @Param("deliveredStatus") ShipmentStatus deliveredStatus,
      @Param("startOfDay") Instant startOfDay);

  @Query(
      """
      select distinct s
      from ShipmentEntity s
      join fetch s.order
      join fetch s.warehouse
      left join fetch s.shipper
      where s.shipper.id = :shipperId
        and upper(s.trackingNumber) = upper(:trackingNumber)
        and s.isDeleted = false
      """)
  Optional<ShipmentEntity> findAssignedByTrackingNumber(
      @Param("shipperId") UUID shipperId, @Param("trackingNumber") String trackingNumber);

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
