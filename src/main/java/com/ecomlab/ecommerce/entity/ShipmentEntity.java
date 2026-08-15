package com.ecomlab.ecommerce.entity;

import com.ecomlab.ecommerce.common.enums.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shipments")
public class ShipmentEntity extends BaseEntity {
  @Id
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "warehouse_id", nullable = false)
  private WarehouseEntity warehouse;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shipper_id")
  private UserEntity shipper;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private ShipmentStatus status = ShipmentStatus.PENDING_PACKING;

  @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ShipmentItemEntity> items = new ArrayList<>();
}
