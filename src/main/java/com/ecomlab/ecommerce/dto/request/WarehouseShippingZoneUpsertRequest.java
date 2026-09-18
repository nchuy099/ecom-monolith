package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseShippingZoneUpsertRequest {
  @NotNull private UUID shippingZoneId;

  @Min(1)
  private int priority;

  @Builder.Default private boolean active = true;
}
