package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.WarehouseEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, UUID> {
  @Query("select w from WarehouseEntity w where w.active=true and w.isDeleted=false")
  List<WarehouseEntity> findActive();

  List<WarehouseEntity> findByIsDeletedFalseOrderByNameAsc();

  Optional<WarehouseEntity> findByIdAndIsDeletedFalse(UUID id);

  boolean existsByCode(String code);
}
