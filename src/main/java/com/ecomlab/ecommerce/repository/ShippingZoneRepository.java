package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.ShippingZoneEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingZoneRepository extends JpaRepository<ShippingZoneEntity, UUID> {
  List<ShippingZoneEntity> findByIsDeletedFalseOrderByNameAsc();

  List<ShippingZoneEntity> findByActiveTrueAndIsDeletedFalseOrderByNameAsc();

  Optional<ShippingZoneEntity> findByIdAndIsDeletedFalse(UUID id);

  boolean existsByCodeAndIsDeletedFalse(String code);
}
