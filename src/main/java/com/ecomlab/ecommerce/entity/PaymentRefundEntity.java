package com.ecomlab.ecommerce.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_refunds")
public class PaymentRefundEntity extends BaseEntity {
  @Id
  @UuidGenerator
  @Column(nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id", nullable = false)
  private PaymentEntity payment;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "return_id", nullable = false, unique = true)
  private ReturnRequestEntity returnRequest;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(name = "provider_reference", nullable = false, length = 255)
  private String providerReference;

  @Column(name = "refunded_at", nullable = false)
  private Instant refundedAt;
}
