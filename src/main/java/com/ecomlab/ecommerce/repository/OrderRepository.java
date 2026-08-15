package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.OrderEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID>, OrderQueryRepository {
  @Query(
      """
      select distinct o from OrderEntity o
      left join fetch o.items i left join fetch i.variant
      where o.id = :orderId and o.user.id = :userId and o.isDeleted = false
      """)
  Optional<OrderEntity> findOwnedByIdWithItems(
      @Param("orderId") UUID orderId, @Param("userId") UUID userId);
}
