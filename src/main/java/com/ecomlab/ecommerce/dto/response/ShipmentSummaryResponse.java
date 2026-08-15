package com.ecomlab.ecommerce.dto.response;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentSummaryResponse {
  private UUID id;
  private String status;
  private String warehouseName;
  private String warehouseAddress;
}
