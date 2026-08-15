package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
  Optional<PaymentEntity> findByIdempotencyKey(String idempotencyKey);

  @Query(
      """
      select p
      from PaymentEntity p
      join fetch p.order
      where p.order.id = :orderId
        and p.isDeleted = false
      order by p.createdAt desc
      """)
  List<PaymentEntity> findActiveByOrderIdWithOrder(@Param("orderId") UUID orderId);
}
