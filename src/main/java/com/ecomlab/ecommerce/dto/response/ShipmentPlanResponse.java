package com.ecomlab.ecommerce.dto.response;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentPlanResponse {
  private UUID warehouseId;
  private String warehouseName;
  private int quantity;
  private double distanceKm;
}
