package com.ecomlab.ecommerce.dto.response;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentItemResponse {
  private UUID id;
  private UUID returnItemId;
  private String sku;
  private String name;
  private int quantity;
}
