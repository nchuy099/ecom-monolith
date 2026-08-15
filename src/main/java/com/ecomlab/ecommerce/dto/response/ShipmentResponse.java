package com.ecomlab.ecommerce.dto.response;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
  private UUID id;
  private UUID orderId;
  private UUID warehouseId;
  private UUID shipperId;
  private String status;
}
