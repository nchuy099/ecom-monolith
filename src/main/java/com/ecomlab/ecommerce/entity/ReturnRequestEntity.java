package com.ecomlab.ecommerce.entity;

import com.ecomlab.ecommerce.common.enums.*;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "returns")
public class ReturnRequestEntity extends BaseEntity {
  @Id
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private ReturnStatus status = ReturnStatus.REQUESTED;

  @Column(nullable = false, length = 1000)
  private String reason;
}
