package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjustInventoryRequest {
  @NotNull private UUID warehouseId;
  @NotNull private UUID variantId;
  private int quantityDelta;
}
