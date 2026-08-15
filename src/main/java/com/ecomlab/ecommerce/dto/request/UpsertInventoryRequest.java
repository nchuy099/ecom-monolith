package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertInventoryRequest {
  @NotNull private UUID warehouseId;
  @NotNull private UUID variantId;

  @Min(0)
  private int availableQuantity;
}
