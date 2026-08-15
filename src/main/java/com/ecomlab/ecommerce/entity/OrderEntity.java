package com.ecomlab.ecommerce.entity;

import com.ecomlab.ecommerce.common.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class OrderEntity extends BaseEntity {
  @Id
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "order_number", nullable = false, unique = true)
  private String orderNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status;

  @Column(name = "recipient_name", nullable = false)
  private String recipientName;

  @Column(nullable = false)
  private String phone;

  @Column(name = "address_line", nullable = false)
  private String addressLine;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false, precision = 9, scale = 6)
  private BigDecimal latitude;

  @Column(nullable = false, precision = 9, scale = 6)
  private BigDecimal longitude;

  @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal totalAmount;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<OrderItemEntity> items = new ArrayList<>();
}
