package com.ecomlab.ecommerce.dto.response;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
  private UUID id;
  private UUID warehouseId;
  private UUID variantId;
  private int availableQuantity;
  private int reservedQuantity;
}
