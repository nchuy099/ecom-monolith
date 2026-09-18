package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
  private UUID id;
  private String orderNumber;
  private String status;
  private BigDecimal totalAmount;
  private String city;
  private String recipientName;
  private String phone;
  private String addressLine;
  private List<OrderItemResponse> items;
  private Instant createdAt;
}
