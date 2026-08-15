package com.ecomlab.ecommerce.entity;

import com.ecomlab.ecommerce.common.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tracking_events")
public class TrackingEventEntity extends BaseEntity {
  @Id
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shipment_id", nullable = false)
  private ShipmentEntity shipment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "actor_id")
  private UserEntity actor;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ShipmentStatus status;

  @Column(length = 1000)
  private String note;

  private BigDecimal latitude;
  private BigDecimal longitude;

  @Column(name = "proof_url")
  private String proofUrl;

  @Column(name = "occurred_at", nullable = false)
  private Instant occurredAt;
}
