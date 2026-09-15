package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnResponse {
  private UUID id;
  private UUID orderId;
  private String status;
  private String reason;
  private String decisionNote;
  private Instant createdAt;
  private Instant updatedAt;
  private BigDecimal refundableAmount;
  private List<ReturnItemResponse> items;
  private List<ShipmentResponse> shipments;
}
