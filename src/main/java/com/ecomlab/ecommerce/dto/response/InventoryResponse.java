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
  private String warehouseName;
  private UUID variantId;
  private String sku;
  private String productName;
  private String warehousePriorityArea;
  private int availableQuantity;
  private int reservedQuantity;
  private Boolean deliverableToSelectedAddress;
}
