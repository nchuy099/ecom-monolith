package com.ecomlab.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shipping_zones")
public class ShippingZoneEntity extends BaseEntity {
  @Id
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false)
  private String name;

  @Column(name = "matched_cities", nullable = false, length = 1000)
  private String matchedCities;

  @Column(nullable = false)
  @Builder.Default
  private boolean active = true;

  @OneToMany(mappedBy = "shippingZone")
  @Builder.Default
  private List<WarehouseShippingZoneEntity> warehouseShippingZones = new ArrayList<>();
}
