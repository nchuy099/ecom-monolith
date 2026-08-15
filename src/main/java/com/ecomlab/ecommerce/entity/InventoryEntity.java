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
@Table(name = "inventories")
public class InventoryEntity extends BaseEntity {
  @Id
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "warehouse_id", nullable = false)
  private WarehouseEntity warehouse;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_variant_id", nullable = false)
  private ProductVariantEntity variant;

  @Column(name = "available_quantity", nullable = false)
  private int availableQuantity;

  @Column(name = "reserved_quantity", nullable = false)
  private int reservedQuantity;

  @Version private long version;

  public void reserve(int q) {
    if (q <= 0 || availableQuantity < q)
      throw new IllegalArgumentException("Insufficient inventory");
    availableQuantity -= q;
    reservedQuantity += q;
  }

  public void release(int q) {
    if (q <= 0 || reservedQuantity < q) {
      throw new IllegalArgumentException("Invalid release");
    }
    reservedQuantity -= q;
    availableQuantity += q;
  }
}
