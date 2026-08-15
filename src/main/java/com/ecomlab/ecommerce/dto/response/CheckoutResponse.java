package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {
  private UUID orderId;
  private String orderNumber;
  private BigDecimal totalAmount;
  private List<ShipmentPlanResponse> shipments;
}
