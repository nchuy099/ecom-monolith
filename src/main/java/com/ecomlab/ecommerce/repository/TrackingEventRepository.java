package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.*;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEventEntity, UUID> {
  @Query(
      """
      select e
      from TrackingEventEntity e
      where e.shipment.id in :shipmentIds
        and e.isDeleted = false
      order by e.occurredAt asc, e.id asc
      """)
  List<TrackingEventEntity> findByShipmentIds(@Param("shipmentIds") List<UUID> shipmentIds);
}
