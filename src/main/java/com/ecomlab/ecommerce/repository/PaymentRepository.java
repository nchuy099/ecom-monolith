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

  @Query(
      """
      select p from PaymentEntity p where p.order.id = :orderId and p.status = com.ecomlab.ecommerce.common.enums.PaymentStatus.SUCCEEDED
        and p.isDeleted = false order by p.paidAt desc
      """)
  List<PaymentEntity> findSucceededByOrderId(@Param("orderId") UUID orderId);

  @Query(
      """
      select p from PaymentEntity p where p.order.id = :orderId and p.provider = 'COD'
        and p.status = com.ecomlab.ecommerce.common.enums.PaymentStatus.PENDING and p.isDeleted = false
      """)
  List<PaymentEntity> findPendingCodByOrderId(@Param("orderId") UUID orderId);
}
