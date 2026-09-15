package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.PaymentRefundEntity;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRefundRepository extends JpaRepository<PaymentRefundEntity, UUID> {
  Optional<PaymentRefundEntity> findByReturnRequestId(UUID returnId);

  @Query("select coalesce(sum(r.amount), 0) from PaymentRefundEntity r where r.payment.id = :paymentId and r.isDeleted = false")
  BigDecimal sumActiveByPaymentId(@Param("paymentId") UUID paymentId);
}
